package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.clubfactory.platform.scheduler.core.utils.StringUtil;
import com.clubfactory.platform.scheduler.engine.mgr.TaskRunnerMgr;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.common.bean.tuple.Tuple2;
import com.clubfactory.platform.scheduler.core.enums.CommandType;
import com.clubfactory.platform.scheduler.core.utils.NumberUtils;
import com.clubfactory.platform.scheduler.core.utils.SysConfigUtil;
import com.clubfactory.platform.scheduler.core.vo.MachineVO;
import com.clubfactory.platform.scheduler.core.vo.ScriptVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.engine.utils.TaskLock;
import com.clubfactory.platform.scheduler.server.constant.Constant;
import com.clubfactory.platform.scheduler.server.dto.DbSlotDto;
import com.clubfactory.platform.scheduler.core.dto.TaskDto;
import com.clubfactory.platform.scheduler.server.utils.BizUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分发作业
 *
 * @author zhoulijiang
 * @date 2020-01-14 15:36
 *
 */
public class TaskSechdeulerRunnable extends SchedulerBaseService implements Runnable  {
	private static final Logger logger = LoggerFactory.getLogger(TaskSechdeulerRunnable.class);
	
	public TaskSechdeulerRunnable(CuratorFramework client, String taskPath, String workPath) {
		super.client = client;
		super.taskPath = taskPath;
		super.workPath = workPath;
		super.init();
	}
	
	@Override
	public void run() {
		// 获取机器信息
		List<MachineVO> machineVOs = machineService.listSlaves();
		if (CollectionUtils.isEmpty(machineVOs)) {
			return;
		}
		// 过滤资源
		machineVOs = machineVOs.stream().filter(machineVO -> machineVO.getSlots() > 0).collect(Collectors.toList());
		// 打乱顺序
		Collections.shuffle(machineVOs);
		
		// 根据总资源数，获取task数量
		int slot = getSlot(machineVOs);
		List<Task> tasks = taskService.listReadyTasksBySlot(slot);
		int readyTaskNum = tasks.size();
		if (CollectionUtils.isEmpty(tasks)) {
			return;
		}
		
		// 根据db的连接数过滤task
		this.filterByDbSlots(tasks);
		
		// machine_id=0的动态赋值ip，ip为null,machine_id!=0,重新找ip
		this.updateIp(machineVOs, tasks);
		
		// 根据槽位分发数据,并修改task状态,返回已经调度的task列表
		List<Task> schedulerTasks = Lists.newArrayList();
		try {
			schedulerTasks = assignWork(machineVOs, tasks);
		} catch (Exception e) {
			logger.error(JSON.toJSONString(tasks), e);
		}

		logger.info("ready 个数 {} -> scheduler 个数 {}",readyTaskNum, schedulerTasks.size());
	}

	private void updateIp(List<MachineVO> machineVOs, List<Task> tasks) {
		// 返回不可用的ip列表
		checkSlave(machineVOs);
		
		// 更新调度机设置为随机调度tas
		Map<Long, String> machineMap = machineService.getAllMap();
		for (Task task : tasks) {
			if (!task.getMachineId().equals(Constant.RAND_MACHINE_ID)) {
				// 针对改了调度机ip的情况
				String ip = machineMap.get(task.getMachineId());
				if (!StringUtils.equals(ip, task.getIp())) {
					task.setIp(ip);
				}
			}
		}
		
	}

	/**
	 * ip检查，不可用的，置为disable
	 * 
	 * @param machineVOs
	 * @return
	 */
	private void checkSlave(List<MachineVO> machineVOs) {
		List<Long> ids = machineVOs.stream().filter(machine -> !super.isHasChild(workPath, machine.getIp())).map(MachineVO::getId).collect(Collectors.toList());
		
		if (CollectionUtils.isNotEmpty(ids)) {
			logger.warn("slave is disable, ip:{}", JSON.toJSONString(ids));
			machineService.disable(ids);
		} 
	}

	private List<Task> assignWork(List<MachineVO> machineVOs, List<Task> tasks) {
		Map<Long, Integer> versionMap = super.getVersionMap(tasks);
		List<ScriptVO> scriptVOs = super.listScript(tasks);
		
		List<Task> newTasks = tasks.stream().distinct().collect(Collectors.toList());
		// 获取正在运行和调度中的实例, tuple2 第一个参数是function, 第二个参数是ip
		Map<Long, Tuple2<String, String>> taskMap = getRunTaskMap();

		// 从dqc获取被阻塞设置的关联任务id
		List<Long> jobIds = tasks.stream().map(Task::getJobId).collect(Collectors.toList());
		List<Long> blockJobIds = dqcRuleService.blockRelJobId(jobIds);
		for (MachineVO machineVO : machineVOs) {
			String function = machineVO.getFunctions();
			Long machineId = machineVO.getId();
			String machineIp = machineVO.getIp();
			// 正在运行的资源数
			int ipSlot = getSlot(taskMap, machineVO);
			
			// 根据资源调度
			int solt = createPathBySolt(tasks, versionMap, scriptVOs, machineId, function, ipSlot, blockJobIds, machineIp);
			if (solt > 0) {
				if (BooleanUtils.isNotTrue(machineVO.getIsSelf())) {
					createPathBySolt(tasks, versionMap, scriptVOs, Constant.RAND_MACHINE_ID, function, ipSlot, blockJobIds, machineIp);
				}
			} else {
				logger.warn("type:{}, ip : {} not slot",machineVO.getFunctions(), machineVO.getIp());
			}
		}
		// 获取已经调度的task列表
		return listSchedulerTasks(tasks, newTasks);
	}

	// 获取剩余资源数
	private int getSlot(Map<Long, Tuple2<String, String>> taskMap, MachineVO machineVO) {
		List<String> childs = super.listChildrens(machineVO.getIp());
		int zkSlot = 0;
		for (String zkTaskId : childs) {
			Long taskId = NumberUtils.stringToLong(zkTaskId);
			if (taskId == null) {
				continue;
			}
			Tuple2<String, String> tuple2 = taskMap.get(taskId);
			if (tuple2 == null) {
				continue;
			}
			if (StringUtils.equals(tuple2.f0, machineVO.getFunctions())
					&& StringUtils.equals(tuple2.f1, machineVO.getIp())) {
				zkSlot++;
			}
		}
		return machineVO.getSlots() - zkSlot;
	}

	private Map<Long, Tuple2<String, String>> getRunTaskMap() {
		List<TaskVO> taskVOs = taskService.listByRunningAndScheduled();
		 
		Map<Long, Tuple2<String, String>> taskMap = Maps.newHashMap();
		for (Task task : taskVOs) {
			String function = "%s_%s";
			String format = String.format(function, task.getCategory().name(),task.getType());
			taskMap.put(task.getId(), new Tuple2<String, String>(format, task.getIp()));
		}
		return taskMap;
	}

	/**
	 * @param tasks 未调度的task列表
	 * @param newTasks 全部task列表
	 * @return
	 */
	private List<Task> listSchedulerTasks(List<Task> tasks, List<Task> newTasks) {
		if (CollectionUtils.isEmpty(tasks)) {
			return newTasks;
		}
		
		List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
		Iterator<Task> iterator = newTasks.iterator();
		while (iterator.hasNext()) {
			Task task = iterator.next();
			if (taskIds.contains(task.getId())) {
				iterator.remove();
			}
		}
		return newTasks;
	}

	public void filterByDbSlots(List<Task> tasks) {
		filterByCateory(tasks, JobCategoryEnum.COLLECT);
		filterByCateory(tasks, JobCategoryEnum.REFLUE);
	}

	private void filterByCateory(List<Task> tasks, JobCategoryEnum jobCategoryEnum) {
		List<Long> jobIds = tasks.stream().filter(task -> jobCategoryEnum == task.getCategory())
				.map(Task::getJobId).distinct().collect(Collectors.toList());
		if (CollectionUtils.isEmpty(jobIds)) {
			return;
		}
		// 获取每个db对应的job列表，和task数目
		List<DbSlotDto> dbSlotDtos = getCollectCountTaskByJobId(tasks, jobIds, jobCategoryEnum);
		if (CollectionUtils.isEmpty(dbSlotDtos)) {
			return;
		}
		// 去除多余的task
		removeTaskByDbSlots(tasks, dbSlotDtos);
	}

	private void removeTaskByDbSlots(List<Task> tasks, List<DbSlotDto> dbSlotDtos) {
		Integer dbSourceSlot = SysConfigUtil.getNumberByKey(Constant.DB_SOURCE_SLOT);
		if (dbSourceSlot == null) {
			return;
		}
		for (DbSlotDto dbSlotDto : dbSlotDtos) {
			int slot = dbSlotDto.getTaskSize() - dbSourceSlot;
			if (slot > 0) {
				Iterator<Task> iterators = tasks.iterator();
				int i = 0;
				while(iterators.hasNext()) {
					Task task = iterators.next();
					if (dbSlotDto.getJobIds().contains(task.getJobId())) {
						iterators.remove();
						i++;
					}
					if (i >= slot) {
						break;
					}
				}
			}
		}
	}

	private List<DbSlotDto> getCollectCountTaskByJobId(List<Task> tasks, List<Long> jobIds, JobCategoryEnum jobCategoryEnum) {
		Map<Long, List<Long>> dbSlotSizeMap = Maps.newHashMap();
		if (JobCategoryEnum.COLLECT == jobCategoryEnum) {
			dbSlotSizeMap = jobCollectService.getDbSlotSizeMap(jobIds);
		} else if (JobCategoryEnum.REFLUE == jobCategoryEnum) {
			dbSlotSizeMap = jobOnlineService.getDbSlotSizeMap(jobIds);
		}
		if (MapUtils.isEmpty(dbSlotSizeMap)) {
			return Lists.newArrayList();
		}
		
		Map<Long, List<Task>> taskMap = tasks.stream().collect(Collectors.groupingBy(Task::getJobId));
		
		List<DbSlotDto> dbSlotDtos = Lists.newArrayList();
		for (Entry<Long, List<Long>> entries : dbSlotSizeMap.entrySet()) {
			DbSlotDto dbSlotDto = new DbSlotDto();
			dbSlotDto.setDbId(entries.getKey());
			for (Long jobId : entries.getValue()) {
				dbSlotDto.getJobIds().add(jobId);
				List<Task> tasksByJobId = taskMap.get(jobId);
				if (tasksByJobId != null) {
					dbSlotDto.setTaskSize(tasksByJobId.size() + dbSlotDto.getTaskSize());
				}
			}
			dbSlotDtos.add(dbSlotDto);
		}
		return dbSlotDtos;
	}

	private int createPathBySolt(List<Task> tasks, Map<Long, Integer> versionMap, List<ScriptVO> scriptVOs,
			  Long machineId, String function, int ipSlot, List<Long> blockJobIds, String machineIp) {
		if (CollectionUtils.isEmpty(tasks)) {
			return ipSlot;
		}
		// 已选定machineId的情况下
		Iterator<Task> iterator = tasks.iterator();
		while(iterator.hasNext()) {
			Task task = iterator.next();
			String taskType = task.getCategory().name() + "_" + task.getType();

			if (machineId.equals(Constant.RAND_MACHINE_ID)) {
				if (StringUtils.equalsAnyIgnoreCase(machineIp, task.getIp())) {
					continue;
				}
			}
			if (machineId.equals(task.getMachineId())
				&& StringUtils.equals(function, taskType)) {
				Long taskId = task.getId();
				if (StringUtils.isBlank(machineIp)) {
					logger.warn("check ip is blank, id:{}, ip:{}",taskId,machineIp);
					continue;
				}
				
				TaskLock taskLock = new TaskLock(super.client, taskId*100);
				if (taskLock.tryLock(1)) {
					try {
						logger.info("scheduler_info, function:{}, ipSlot:{}, machineId:{}, taskId:{}, taskstatus:{}, ip:{}",function,ipSlot,machineId,taskId, task.getStatus().name(), machineIp);
						//taskService.updateByReady(taskId, machineIp);
						createPath(task, versionMap, scriptVOs, blockJobIds, machineIp);
					} catch (Exception e) {
						logger.error("create path error, taskId:{}, ip:{}", taskId, machineIp, e);
					} finally {
						taskLock.unlock();
					}
					iterator.remove();
					ipSlot--;
				}
			}
			if (ipSlot <= 0) {
				break;
			}
		}
		return ipSlot;
	}
	

	private void createPath(Task task, Map<Long, Integer> versionMap, List<ScriptVO> scriptVOs, List<Long> blockJobIds, String machineIp) {
		if (StringUtils.isBlank(machineIp)) {
			return;
		}
		Map<Long, String> fileNameMap = scriptVOs.stream().collect(Collectors.toMap(ScriptVO::getId, ScriptVO::getFileName));
		Map<Long, Long> scriptUserMap = scriptVOs.stream().collect(Collectors.toMap(ScriptVO::getId, ScriptVO::getCreateUser));
		
		String path = super.getPath(machineIp, task.getId());
		TaskDto taskDto = new TaskDto();
		taskDto.setStatus(TaskStatusEnum.SCHEDULED);
		taskDto.setCommandType(CommandType.SCHEDULER);
		taskDto.setVersion(versionMap.get(task.getId()));
		taskDto.setFileName(fileNameMap.get(task.getScriptId()));
		taskDto.setScriptUserId(scriptUserMap.get(task.getScriptId()));
		if (blockJobIds.contains(task.getJobId())) {
			taskDto.setIsBlock(true);
		} else {
			taskDto.setIsBlock(false);
		}
		
		super.setZkData(path, taskDto);
	}

	private int getSlot(List<MachineVO> machineVOs) {
		int totalSlot = machineVOs.stream().mapToInt(MachineVO::getSlots).sum();
		
		List<String> allChilrens = Lists.newArrayList();
		for (MachineVO machineVO : machineVOs) {
			List<String> childs = super.listChildrens(machineVO.getIp());
			allChilrens.addAll(childs);
		}
		int slot = totalSlot - allChilrens.size();
		return slot * 3;
	}

}
