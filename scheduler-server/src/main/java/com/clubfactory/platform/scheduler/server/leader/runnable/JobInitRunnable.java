package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.scheduler.common.utils.TaskDependsUtil;
import com.clubfactory.platform.scheduler.core.service.impl.DqcRuleService;
import com.clubfactory.platform.scheduler.core.utils.SysConfigUtil;
import com.clubfactory.platform.scheduler.core.vo.DqcRuleVO;
import com.clubfactory.platform.scheduler.dal.enums.*;
import com.clubfactory.platform.scheduler.dal.po.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.common.enums.ResourceType;
import com.clubfactory.platform.scheduler.core.service.impl.LockService;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineDependsVO;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.dal.dto.SchedulerTimeDto;
import com.clubfactory.platform.scheduler.dal.dto.TaskTimeDto;
import com.clubfactory.platform.scheduler.server.utils.BizUtils;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

import static com.clubfactory.platform.scheduler.server.constant.Constant.DQC_TASK_NOT_BLOCK_TIME;
import static com.clubfactory.platform.scheduler.server.constant.Constant.DQC_TASK_NOT_BLOCK_TIME_END;

/**
 * 任务初始化为实例
 *
 * @author zhoulijiang
 * @date 2019-12-12 11:04
 *
 */
@Service
@Slf4j
public class JobInitRunnable extends SchedulerBaseService implements Runnable {
	
	@Resource
	LockService lockService;
	
	private Boolean isLeader;
	
	private static final int MAX_VALUE = Integer.MAX_VALUE;
	private static final int VIRTUAL_NODE = 1;
	private static final int LOCK_SEC = 60 * 30;
	
	public void setIsLeader(Boolean isLeader) {
		this.isLeader = isLeader;
	}

	@Scheduled(cron="0 30 23 * * ?")
	public void fixedTimeRun() {
		if (BooleanUtils.isNotTrue(isLeader)) {
			return;
		}
		super.init();
		
		if (lockService.tryLockOnce(ResourceType.TASK_INT_LOCK, LOCK_SEC)) {
			Date date = new Date();
			
			log.info("jobInitFixedTimeRun");
			try {
				fixedTimeRunNow();
				notice(date);
			} finally {
				lockService.unlock(ResourceType.TASK_INT_LOCK);
			}
		}
	}

	private void notice(Date date) {
		List<Task> tasks = taskService.listByDate(date);
		noticeBizService.sendTaskNotice(tasks);
	}

	@Transactional
	public void fixedTimeRunNow() {
		log.info("---------------  任务初始化开始 ----------------");
		
		// 当晚生成次日凌晨的实例
		Date nowDate = DateUtil.getMinOfDay(DateUtil.getDate(LocalDate.now().plusDays(1)));
		
		List<JobOnlineVO> jobs = jobOnlineService.listJob(false);
		if (CollectionUtils.isEmpty(jobs)) {
			return;
		}
		
		// 实时的任务不动态生成
		this.removeRealTimeJob(jobs);
		
		// 生成实例，并保存
		List<Long> jobIds = jobs.stream().map(JobOnlineVO::getJobId).collect(Collectors.toList());
		final List<JobOnlineDependsVO> jobDependsVOs = jobOnlineDependsService.listJobDepends(jobIds);
		List<Task> tasks = saveTasks(nowDate, jobs, jobDependsVOs);
		if (CollectionUtils.isEmpty(tasks)) {
			return;
		}
		
		// 实例依赖生成
		try {
			this.generateTaskDepends(jobs, tasks, jobDependsVOs, nowDate);
		} catch (Exception e) {
			log.error("generate task depends error", e);
		}
		
		log.info("---------------  任务初始化结束 ----------------");
	}
	
	
	@Override
	public void run() {
		super.init();
		runNow();
	}
	
	@Transactional
	public void runNow() {
		// 实例生成
		Date nowDate = new Date();
		List<JobOnlineVO> jobs = jobOnlineService.listJob(true);
		if (CollectionUtils.isEmpty(jobs)) {
			return;
		}
		// 实时的任务不动态生成
		this.removeRealTimeJob(jobs);
		
		List<Long> jobIds = jobs.stream().map(JobOnlineVO::getJobId).distinct().collect(Collectors.toList());
		// 立即执行的任务，只会有一次
		jobOnlineService.fixOneTime(jobIds);
		
		// 保存节点
		final List<JobOnlineDependsVO> jobDependsVOs = jobOnlineDependsService.listJobDepends(jobIds);
		List<Task> tasks = saveTasks(nowDate, jobs, jobDependsVOs);
		if (CollectionUtils.isNotEmpty(tasks)) {
			// 实例依赖生成
			try {
				this.generateNowTaskDepends(jobs, jobDependsVOs, tasks);
			} catch (Exception e) {
				log.error("generate task depends now error", e);
			}
		}
	}

	private void removeRealTimeJob(List<JobOnlineVO> jobs) {
		if (CollectionUtils.isEmpty(jobs)) {
			return;
		}
		
		Iterator<JobOnlineVO> iterator = jobs.iterator();
		while(iterator.hasNext()) {
			JobOnlineVO jobOnlineVO = iterator.next();
			if (jobOnlineVO.getCycleType() == JobCycleTypeEnum.REAL_TIME) {
				iterator.remove();
			}
		}
	}

	private void generateNowTaskDepends(List<JobOnlineVO> jobs, final List<JobOnlineDependsVO> jobDependsVOs
			,List<Task> tasks) {
		if (CollectionUtils.isEmpty(jobDependsVOs)) {
			return;
		}
		// 今天凌晨
		Date nowDate = DateUtil.getMinOfDay(DateUtil.getDate(LocalDate.now()));
		this.genDepends(tasks, jobs, jobDependsVOs, nowDate);
	}
	
	private void genDepends(List<Task> tasks, List<JobOnlineVO> jobOnlines, List<JobOnlineDependsVO> jobOnlineDependsVOs, Date date) {
		Map<DependTypeEnum, List<JobOnlineDependsVO>> dependsMap = getDependsMap(jobOnlineDependsVOs);
		// 自依赖
		if (CollectionUtils.isNotEmpty(dependsMap.get(DependTypeEnum.SELF))) {
			List<JobOnline> selfJobOnlines = listJobOnlinesByType(jobOnlines, dependsMap.get(DependTypeEnum.SELF));
			if (CollectionUtils.isNotEmpty(selfJobOnlines)) {
				List<TaskDepends> taskDependsList = taskDependsBizService.genSelfDepends(selfJobOnlines, tasks);
				taskDependsService.saveBatch(taskDependsList);
			}
		}
		// 同周期
		if (CollectionUtils.isNotEmpty(dependsMap.get(DependTypeEnum.SAME))) {
			List<JobOnlineDependsVO> jobOnlineDepends = dependsMap.get(DependTypeEnum.SAME);
			List<TaskDepends> taskDependsList = taskDependsBizService.genSameDepends(jobOnlines, jobOnlineDepends, tasks, date);
			taskDependsService.saveBatch(taskDependsList);
		}
		// 上一周期
		if (CollectionUtils.isNotEmpty(dependsMap.get(DependTypeEnum.PREV))) {
			List<JobOnlineDependsVO> jobOnlineDepends = dependsMap.get(DependTypeEnum.PREV);
			List<TaskDepends> taskDependsList = taskDependsBizService.genPrevDepends(jobOnlines, jobOnlineDepends, tasks, date);
			taskDependsService.saveBatch(taskDependsList);
		}
		// 全周期
		if (CollectionUtils.isNotEmpty(dependsMap.get(DependTypeEnum.ALL))) {
			List<JobOnlineDependsVO> jobOnlineDepends = dependsMap.get(DependTypeEnum.ALL);
			List<TaskDepends> taskDependsList = taskDependsBizService.genAllDepends(jobOnlines, jobOnlineDepends, tasks, date);
			taskDependsService.saveBatch(taskDependsList);
		}
		// dqc依赖
		genDqcDepends(tasks, jobOnlines);

	}

	private void genDqcDepends(List<Task> tasks, List<JobOnlineVO> jobOnlines) {
		// DQC依赖
		List<Long> jobIds = jobOnlines.stream().map(JobOnline::getJobId).collect(Collectors.toList());
		List<DqcRuleVO> dqcRules = dqcRuleService.listByRelJobIds(jobIds);
		if (CollectionUtils.isEmpty(dqcRules)) {
			return;
		}
		List<TaskDepends> dependsList = Lists.newLinkedList();
		List<Long> dqcJobIds = dqcRules.stream().map(DqcRule::getJobId).distinct().collect(Collectors.toList());
		Map<Long, List<Task>> dqcTaskMap = tasks.stream().filter(task -> dqcJobIds.contains(task.getJobId())).collect(Collectors.groupingBy(Task::getJobId));

		List<Long> targetJobIds = dqcRules.stream().map(DqcRule::getRelJobId).distinct().collect(Collectors.toList());
		Map<Long, List<Task>> targetTaskMap = tasks.stream().filter(task -> targetJobIds.contains(task.getJobId())).collect(Collectors.groupingBy(Task::getJobId));
		for (DqcRule dqcRule : dqcRules) {
			List<Task> dqcTasks = dqcTaskMap.get(dqcRule.getJobId());
			List<Task> targetTasks = targetTaskMap.get(dqcRule.getRelJobId());
			if (CollectionUtils.isEmpty(dqcTasks) || CollectionUtils.isEmpty(targetTasks)) {
				continue;
			}
			for (Task task : targetTasks) {
				for (Task dqcTask : dqcTasks) {
					if (task.getTaskTime().equals(dqcTask.getTaskTime())) {
						dependsList.add(TaskDependsUtil.addTaskDepends(dqcTask.getId()
								, task.getId()
								, dqcTask.getCreateUser()));
					}
				}
			}
		}
		taskDependsService.saveBatch(dependsList);
	}

	private List<JobOnline> listJobOnlinesByType(List<JobOnlineVO> jobOnlines, List<JobOnlineDependsVO> JobOnlineDepends) {
		List<Long> jobIds = JobOnlineDepends.stream().map(JobOnlineDependsVO::getJobId).collect(Collectors.toList());
		List<JobOnline> selfJobOnlines = jobOnlines.stream().filter(job -> jobIds.contains(job.getJobId())).collect(Collectors.toList());
		return selfJobOnlines;
	}
	
	private Map<DependTypeEnum, List<JobOnlineDependsVO>> getDependsMap(List<JobOnlineDependsVO> jobOnlineDependsVOs) {
		return jobOnlineDependsVOs.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getType));
	}

	private List<Task> saveTasks(Date nowDate, List<JobOnlineVO> jobs, List<JobOnlineDependsVO> jobDependsVOs) {
		List<Task> tasks = this.genTasks(nowDate, jobs);
		if (CollectionUtils.isEmpty(tasks)) {
			return Lists.newArrayList();
		}

		// dqc非阻塞实例延迟调度
		editDqcNotBlockStartTime(tasks);

		// 获取等级
		genScore(jobs, jobDependsVOs, tasks);

		log.info("生成实例 个数 {}", tasks.size());
		
		taskService.saveBatch(tasks);
		return tasks;
	}

	private void genScore(List<JobOnlineVO> jobs, List<JobOnlineDependsVO> jobDependsVOs, List<Task> tasks) {
		if (CollectionUtils.isNotEmpty(jobDependsVOs)) {
			Map<Long, Integer> levelMap = Maps.newHashMap();
			try {
				levelMap = this.getSchedulerValueMap(jobs, jobDependsVOs);
			} catch (Exception e) {
				log.error("generate score error", e);
			}
			for (Task task : tasks) {
				task.setScore(levelMap.get(task.getJobId()));
			}
		}
	}

	private void editDqcNotBlockStartTime(List<Task> tasks) {
		List<Long> notBlockJobIds = dqcRuleService.listNotBlockJobId();
		Date startNotBlockTime = getDurDate(DQC_TASK_NOT_BLOCK_TIME);
		Date endNotBlockTime = getDurDate(DQC_TASK_NOT_BLOCK_TIME_END);

		for (Task task : tasks) {
			if (JobTypeEnum.NORMAL == task.getJobType()) {
				continue;
			}
			if (!notBlockJobIds.contains(task.getJobId())) {
				continue;
			}
			if (startNotBlockTime.after(task.getStartTime())
				&& endNotBlockTime.before(task.getStartTime())) {
				task.setStartTime(endNotBlockTime);
			}
		}
	}

	private Date getDurDate(String key) {
		String time = SysConfigUtil.getByKey(key);
		String date = LocalDate.now().plusDays(1).toString() + " " + time;
		return DateUtil.parse(date, DateFormatPattern.YYYY_MM_DD_HH_MM_SS);
	}

	private List<Task> genTasks(Date nowDate, List<JobOnlineVO> jobs) {
		Assert.notNull(nowDate);
		Assert.collectionNotEmpty(jobs, "jobs");
		
		List<Long> ids = jobs.stream().map(JobOnlineVO::getMachineId).distinct().collect(Collectors.toList());
		Map<Long, String> machineMap = machineService.getMap(ids);
		
		Map<String, List<String>> ipMap = machineService.getIpsByJobTypeMap();
		List<Task> tasks = Lists.newArrayList();
		for (JobOnlineVO job : jobs) {
			try {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(job.getSchedulerTime(), SchedulerTimeDto.class);
				// 月任务，周任务，不是当日跑的，不再生成
				if (job.getCycleType() == null) {
					continue;
				}
				if (job.getCycleType().isNotAllowCreateByDay(schedulerTimeDto, nowDate)) {
					continue;
				}
				List<TaskTimeDto> taskTimeDtos = job.getCycleType().listTaskTimes(schedulerTimeDto, nowDate);
				for (TaskTimeDto timeDto : taskTimeDtos) {
					Task task = buildTask(job);
					task.setTaskTime(timeDto.getTaskTime());
					task.setStartTime(timeDto.getStartTime());
					if (machineMap.get(job.getMachineId()) != null) {
						task.setIp(machineMap.get(job.getMachineId()));
					} else {
						String ip =  BizUtils.getRandIp(ipMap, job.getCategroy(), job.getType());
						task.setIp(ip);
					}
					tasks.add(task);
				}
			} catch (Exception e) {
				log.error("genTasks job_id: {}", job.getJobId(), e);
			}

		}
		return tasks;
	}

	public Map<Long, Integer> getSchedulerValueMap(List<JobOnlineVO> jobs, List<JobOnlineDependsVO> jobDependsVOs) {
		// 剔除自依赖的场景
		List<JobOnlineDependsVO> depends = jobDependsVOs.stream().collect(Collectors.toList());
		Iterator<JobOnlineDependsVO> iterator = depends.iterator();
		while(iterator.hasNext()) {
			JobOnlineDependsVO dependsVO = iterator.next();
			if (dependsVO.getJobId().equals(dependsVO.getParentId())) {
				iterator.remove();
			}
		}
		// 没有父节点，就是根节点，值为1
		Map<Long, Integer> jobMap = genTopTree(jobs, depends);
		this.genTree(depends, jobMap);
		Map<Long, Integer> jobValueMap = this.genValue(depends, jobMap);
		return jobValueMap;
	}

	// 获取所有根节点
	private Map<Long, Integer> genTopTree(List<JobOnlineVO> jobs, List<JobOnlineDependsVO> depends) {
		int level = 1;
		// 所有子节点对应的父节点列表
		Map<Long, List<JobOnlineDependsVO>> parentMap = depends.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getJobId));
		Map<Long, Integer> jobMap = Maps.newHashMap();
		for (JobOnlineVO job : jobs) {
			// 如果该子节点没有父节点，就是跟节点
			if (parentMap.get(job.getJobId()) == null) {
				jobMap.put(job.getJobId(), level);
			}
		}
		return jobMap;
	}

	/**
	 * 获取值
	 *
	 * @param depends
	 * @param jobMap 所有节点，值为层级
	 * @return
	 */
	private Map<Long, Integer> genValue(List<JobOnlineDependsVO> depends, Map<Long, Integer> jobMap) {
		// 当前值是最小孩子的值+自己的值
		Map<Long, Integer> newJobMap = Maps.newHashMap();
		Map<Long, List<JobOnlineDependsVO>> childrensMap = depends.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getParentId));
		for (JobOnlineDependsVO depend : depends) {
			// 当前父节点下的所有子节点
			List<JobOnlineDependsVO> childrens = childrensMap.get(depend.getParentId());
			if (CollectionUtils.isEmpty(childrens)) {
				continue;
			}

			// 求出当前父节点下所有子节点最小的值
			int value = MAX_VALUE;
			for (JobOnlineDependsVO children : childrens) {
				Integer integer = jobMap.get(children.getJobId());
				if (integer == null) {
					continue;
				}
				if (integer < value) {
					value = integer;
				}
			}
			// 获取当前父节点。
			// 父节点是根节点，父节点的值等于当前父节点下所有子节点最小的值+父节点的值
			// 父节点不是根节点，父节点的值等于当前父节点下所有子节点最小的值
			Integer curValue = jobMap.get(depend.getParentId());
			if (curValue != null) {
				newJobMap.put(depend.getParentId(), value+curValue);
			} else {
				newJobMap.put(depend.getParentId(), value);
			}
		}
		
		
		// 叶子节点最大父节点调度值+1
		Map<Long, List<JobOnlineDependsVO>> parentsMap = depends.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getJobId));
		for (JobOnlineDependsVO depend : depends) {
			// 判定叶子节点
			Long jobId = depend.getJobId();
			if (newJobMap.get(jobId) == null) {
				List<JobOnlineDependsVO> parents = parentsMap.get(jobId);
				int value = 0;
				for (JobOnlineDependsVO parent : parents) {
					if (newJobMap.get(parent.getParentId()) > value) {
						value = newJobMap.get(parent.getParentId());
					}
				}
				newJobMap.put(jobId, value+VIRTUAL_NODE);
			}
		}
		
		return newJobMap;
	}

	/**
	 *
	 * @param depends
	 * @param jobMap 根节点map，值为1
	 */
	private void genTree(List<JobOnlineDependsVO> depends, Map<Long, Integer> jobMap) {
		Map<Long, List<JobOnlineDependsVO>> parentsMap = depends.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getJobId));
		int level = 0;
		for (int i = 0; i <= depends.size(); i++) {
			for (JobOnlineDependsVO den : depends) {
				// 父节点没有层级，或者跟当前需要层级不一致
				Integer jobLevel = jobMap.get(den.getParentId());
				if (jobLevel == null || jobLevel != level) {
					continue;
				}
				
				// 如果该节点的，其中一个父节点信息没有标识层级的，直接跳过
				List<JobOnlineDependsVO> parents = parentsMap.get(den.getJobId());
				Boolean isOk = true;
				for (JobOnlineDependsVO onlineDependsVO : parents) {
					if (jobMap.get(onlineDependsVO.getParentId()) == null) {
						isOk = false;
					}
				}

				if (isOk.equals(false)) {
					continue;
				}
				// 层级+1
				jobMap.put(den.getJobId(), level + 1);
				//i++;
			}
			level++;
		}
	}
	
	/**
	 * 生成依赖节点列表 
	 * 
	 * @param jobs 本次要要生成的所有任务数据
	 * @param tasks 本次生成的所有task节点数据
	 * @param jobDependsVOs job中最新的taskId Map数据
	 */
	private void generateTaskDepends(List<JobOnlineVO> jobs, List<Task> tasks, List<JobOnlineDependsVO> jobDependsVOs, Date nowDate) {
		if (CollectionUtils.isEmpty(jobDependsVOs)) {
			return;
		}
		// 自依赖
		this.genDepends(tasks, jobs, jobDependsVOs, nowDate);
	}



	private Task buildTask(JobOnlineVO job) {
		Task task = new Task();
		task.setJobId(job.getJobId());
		task.setName(job.getName());
		task.setCategory(job.getCategroy());
		task.setClusterId(job.getClusterId());
		task.setPriority(job.getPriority());
		if (JobStatusEnum.PAUSE == job.getStatus()) {
			task.setStatus(TaskStatusEnum.PAUSE);
		} else {
			task.setStatus(TaskStatusEnum.INIT);
		}
		task.setMachineId(job.getMachineId());
		task.setRetryCount(0);
		task.setNoticeCount(0);
		task.setScore(0);
		task.setIsNotice(false);
		task.setDepartName(job.getDepartName());
		task.setType(job.getType());
		task.setCreateUser(job.getCreateUser());
		task.setUpdateUser(job.getUpdateUser());
		task.setJobType(job.getJobType());
		task.setCycleType(job.getCycleType());
		task.setRetryMax(job.getRetryMax());
		task.setScriptId(job.getScriptId());
		task.setRetryDur(job.getRetryDur());
		task.setIsTemp(false);
		return task;
	}
	
}
