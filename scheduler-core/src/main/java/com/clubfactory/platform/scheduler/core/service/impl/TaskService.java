package com.clubfactory.platform.scheduler.core.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.clubfactory.platform.scheduler.dal.enums.JobTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.common.bean.PageUtils;
import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.dao.TaskMapper;
import com.clubfactory.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.Task;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskService extends BaseNewService<TaskVO,Task> {

    @Resource
    TaskMapper taskMapper;
    
    private static int NOTICE_DAY = 7;

    @PostConstruct
    public void init(){
        setBaseMapper(taskMapper);
    }

    public List<TaskVO> listByRunningAndScheduled() {
    	Task task = new Task();
		task.setIdsString(Lists.newArrayList(TaskStatusEnum.RUNNING.name()
				, TaskStatusEnum.SCHEDULED.name()));
		task.setQueryListFieldName("status");
		task.setIsDeleted(false);
		task.setIsTemp(false);
		return this.list(task);
    }

	public List<TaskVO> listIdByDqcTask(List<Long> taskIds) {
    	if (CollectionUtils.isEmpty(taskIds)) {
    		return Lists.newArrayList();
		}
		Task task = new Task();
		task.setJobType(JobTypeEnum.DQC);
		task.setIds(taskIds);
		task.setIsDeleted(false);
		return this.list(task);
	}
    
    public void initReadyByWorkFailed(String ip, String endTime) {
    	Assert.notNull(ip);
    	
    	Task task = new Task();
    	task.setStatus(TaskStatusEnum.RUNNING);
    	task.setIp(ip);
    	task.setEndDate(endTime);
    	task.setCycleType(JobCycleTypeEnum.REAL_TIME);
    	task.setIsDeleted(false);
    	
    	Map<String, Object> updateParam = Maps.newHashMap();
    	updateParam.put("status", TaskStatusEnum.READY);
    	task.setUpdateParam(updateParam);
    	taskMapper.editByUpdateTime(task);
    }
    
    public List<Task> listByDate(Date date) {
    	Task task = new Task();
    	task.setIsDeleted(false);
    	task.setStartDate(DateUtil.format(date, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
    	return taskMapper.list(task);
    }

    public void updateByReady(Long id, String ip) {
    	Assert.notNull(id);

    	Task task = new Task();
    	task.setId(id);
    	task.setStatus(TaskStatusEnum.READY);
    	Map<String, Object> updateParam = Maps.newHashMap();
    	updateParam.put("status", TaskStatusEnum.SCHEDULED);
		updateParam.put("ip", ip);
    	task.setUpdateParam(updateParam);
    	this.edit(task);
    }
    
    public List<Task> listReadyTasksBySlot(int slot) {
    	Task task = new Task();
    	task.setIsDeleted(false);
    	task.setStatus(TaskStatusEnum.READY);
    	task.setPageSize(slot);
    	task.setPageNo(1);
    	task.setOrderBy("priority desc, score asc, start_time asc");
    	PageUtils<Task> page = this.pageList(task);
    	return page.getRows();
    }
    
    /**
     * 状态改为ready
     *
     * @param ids
     */
    public void updateReady(List<Long> ids) {
    	Assert.collectionNotEmpty(ids, "id列表");
    	Set<Long> setIds = Sets.newHashSet(ids);
    	log.info("init -> ready 个数 {} ", setIds.size());
    	
    	Task task = new Task();
    	task.setStatus(TaskStatusEnum.INIT);
    	task.setIds(Lists.newArrayList(setIds));
    	Map<String, Object> updateParam = Maps.newHashMap();
    	updateParam.put("status", TaskStatusEnum.READY);
    	task.setUpdateParam(updateParam);
    	this.edit(task);
    }

	public void updateByLost(Long id) {
		if (id == null) {
			return;
		}

		Task task = new Task();
		task.setStatus(TaskStatusEnum.SCHEDULED);
		Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status", TaskStatusEnum.READY);
		task.setUpdateParam(updateParam);
		this.edit(task);
	}
    
    public TaskVO get(Long id) {
    	Assert.nonNull(id, "TaskId为空");
    	Task task = new Task();
    	task.setId(id);
    	task.setIsDeleted(false);
    	return this.get(task);
    }
    
    public void updateInitByTasks(List<Task> tasks) {
    	Assert.collectionNotEmpty(tasks, "task列表");
    	Date date = new Date();
    	Map<Integer, List<Task>> mapRetryTask = tasks.stream().collect(Collectors.groupingBy(Task::getRetryCount));
    	for (Entry<Integer, List<Task>> key : mapRetryTask.entrySet()) {
    		List<Long> ids = key.getValue().stream().map(Task::getId).collect(Collectors.toList());
    		
    		Task task = new Task();
        	task.setIds(ids);
        	Map<String, Object> updateParam = Maps.newHashMap();
        	updateParam.put("status", TaskStatusEnum.INIT);
        	//updateParam.put("retry_count", key.getKey());
			updateParam.put("notice_time", date);
			updateParam.put("end_time", null);
        	task.setUpdateParam(updateParam);
        	this.editIfNull(task);
    	}
    }
    
    public void updateInitAndIpByTasks(List<Task> tasks) {
    	Assert.collectionNotEmpty(tasks, "task列表");
    	Date date = new Date();
    	for (Task task : tasks) {
    		Task newTask = new Task();
    		newTask.setId(task.getId());
        	Map<String, Object> updateParam = Maps.newHashMap();
        	updateParam.put("status", TaskStatusEnum.INIT);
			updateParam.put("notice_time", date);
			updateParam.put("end_time", null);
			newTask.setUpdateParam(updateParam);
        	this.editIfNull(newTask);
    	}
    }

    public Boolean taskStateRunning(Long taskId) {
    	Assert.notNull(taskId);
		int num = taskMapper.setTaskRunning(taskId);
		if (num > 0) {
			return true;
		} else {
			return false;
		}
	}
    
    public int editIfNull(Task task) {
    	Assert.notNull(task);
    	
    	task.setUpdateTime(new Date());
    	return taskMapper.editIfNull(task);
    }
    
	public void updateByTasks(List<Task> tasks) {
		Assert.collectionNotEmpty(tasks, "task列表");
		
		for (Task taskVO : tasks) {
			Task task = new Task();
			task.setId(taskVO.getId());
			
			Map<String, Object> updateParam = Maps.newHashMap();
			updateParam.put("is_notice", true);
			updateParam.put("notice_time", new Date());
			updateParam.put("notice_count", Optional.ofNullable(taskVO.getNoticeCount()).orElse(0) + 1);
			task.setUpdateParam(updateParam);
        	this.edit(task);
		}
	}

	public void updateIsNoticeByTasks(List<Task> tasks) {
		Assert.collectionNotEmpty(tasks, "task列表");

		for (Task taskVO : tasks) {
			Task task = new Task();
			task.setId(taskVO.getId());

			Map<String, Object> updateParam = Maps.newHashMap();
			updateParam.put("notice_time", new Date());
			updateParam.put("is_notice", taskVO.getIsNotice());
			updateParam.put("notice_count", Optional.ofNullable(taskVO.getNoticeCount()).orElse(0) + 1);
			task.setUpdateParam(updateParam);
			this.edit(task);
		}
	}
	
	public Boolean updateNotice(List<Long> taskIds) {
		Assert.collectionNotEmpty(taskIds, "taskIds");
		Task task = new Task();
		task.setIds(taskIds);
		Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("notice_time", new Date());
		updateParam.put("update_time", new Date());
		task.setUpdateParam(updateParam);
		taskMapper.editNotice(task);
		return true;
	}

	public Boolean updateSuccess(List<Long> taskIds) {
		if (CollectionUtils.isEmpty(taskIds)) {
			return true;
		}
		Task task = new Task();
		task.setIds(taskIds);
		Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status", TaskStatusEnum.SUCCESS);
		updateParam.put("end_time", new Date());
		task.setUpdateParam(updateParam);
		this.edit(task);
		return true;
	}

	public Boolean updateDataFailed(List<Long> taskIds) {
		if (CollectionUtils.isEmpty(taskIds)) {
			return true;
		}
		Task task = new Task();
		task.setIds(taskIds);
		Map<String, Object> updateParam = Maps.newHashMap();
		updateParam.put("status", TaskStatusEnum.DATA_FAILED);
		updateParam.put("end_time", new Date());
		task.setUpdateParam(updateParam);
		this.edit(task);
		return true;
	}
	
    public List<Long> listBySuccess(List<Long> ids) {
    	Task task = new Task();
    	task.setStatus(TaskStatusEnum.SUCCESS);
    	task.setIsDeleted(false);
    	task.setIds(ids);
    	List<TaskVO> taskVOs = this.list(task);
    	if (CollectionUtils.isEmpty(taskVOs)) {
    		return Lists.newArrayList();
    	}
    	return taskVOs.stream().map(TaskVO::getId).collect(Collectors.toList());
    }
    
    public List<Task> listMaxTask() {
    	Date queryDate = new Date(System.currentTimeMillis() - 10 * 1000L);
    	String date = DateUtil.format(queryDate, DateFormatPattern.YYYY_MM_DD_HH_MM_SS);
    	return taskMapper.listMaxTask(date);
    }
    
    public Map<Long,List<Task>> getAllTaskMapByWeek(Date date) {
    	LocalDate endlocalDate = DateUtil.getLocalDate(date);
    	LocalDate startLocalDate = endlocalDate.minusWeeks(1);
    	
    	return getTaskMapByDate(endlocalDate, startLocalDate);
    }
    
    public Map<Long,List<Task>> getSameTaskMapByWeek(Date date) {
    	LocalDate endlocalDate = DateUtil.getLocalDate(date);
    	LocalDate startLocalDate = endlocalDate.minusDays(1);
    	
    	return getTaskMapByDate(endlocalDate, startLocalDate);
    }
    
    public Map<Long,List<Task>> getAllTaskMapByMonth(Date date) {
    	LocalDate localDate = DateUtil.getLocalDate(date);
    	LocalDate minusMonths = localDate.minusMonths(1);
		LocalDate startLocalDate = LocalDate.of(minusMonths.getYear(),minusMonths.getMonth(),1);
    	LocalDate endLocalDate = LocalDate.of(localDate.getYear(),localDate.getMonth(),1);
    	
    	return getTaskMapByDate(endLocalDate, startLocalDate);
    }
    
    public Map<Long,List<Task>> getSameTaskMapByMonth(Date date) {
    	LocalDate localDate = DateUtil.getLocalDate(date);
    	LocalDate endLocalDate = LocalDate.of(localDate.getYear(),localDate.getMonth(),1);
    	LocalDate startLocalDate = endLocalDate.minusDays(1);
    	
    	return getTaskMapByDate(endLocalDate, startLocalDate);
    }

	public Map<Long,List<Task>> getYesterdayTaskMap(Date date) {
		LocalDate endLocalDate = DateUtil.getLocalDate(date);
		LocalDate startLocalDate = endLocalDate.minusDays(1);

		return getTaskMapByDate(endLocalDate, startLocalDate);
	}

	private Map<Long, List<Task>> getTaskMapByDate(LocalDate endLocalDate, LocalDate startLocalDate) {
		Task task = new Task();
    	task.setStartDate(startLocalDate.toString());
    	task.setEndDate(endLocalDate.toString());
		task.setIsTemp(false);
    	List<Task> tasks = taskMapper.listByDate(task);
    	if (CollectionUtils.isEmpty(tasks)) {
    		return Maps.newHashMap();
    	}
    	return tasks.stream().collect(Collectors.groupingBy(Task::getJobId));
	}

	private Map<Long, List<Task>> getPrevTaskMapByDate(LocalDate endLocalDate, LocalDate startLocalDate) {
		Task task = new Task();
		task.setStartDate(startLocalDate.toString());
		task.setEndDate(endLocalDate.toString());
		task.setIsTemp(false);
		List<Task> tasks = taskMapper.listByDate(task);
		if (CollectionUtils.isEmpty(tasks)) {
			return Maps.newHashMap();
		}
		return tasks.stream().collect(Collectors.groupingBy(Task::getJobId));
	}

    public List<TaskVO> listIdsByStartTask(Date nowDate) {
    	Task task = new Task();
    	task.setIsDeleted(false);
    	task.setStatus(TaskStatusEnum.INIT);
    	task.setEndDate(DateUtil.format(nowDate, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
    	List<TaskVO> taskVOs = this.list(task);
    	if (CollectionUtils.isEmpty(taskVOs)) {
    		return Lists.newArrayList();
    	}
    	return taskVOs;
    }
    
    public List<Task> listByStatus(TaskStatusEnum statusEnum) {
    	Task task = new Task();
    	task.setIsDeleted(false);
    	task.setStatus(statusEnum);
    	List<Task> taskVOs = taskMapper.list(task);
    	if (CollectionUtils.isEmpty(taskVOs)) {
    		return Lists.newArrayList();
    	}
    	return taskVOs;
    }
    
    public List<Task> listByDataFailedNotice() {
    	LocalDate localDate = getNoticeStartTime();
    	return taskMapper.listByDataFailedNotice(localDate.toString());
    }
    
    public List<Task> listByFailedNotice() {
    	LocalDate localDate = getNoticeStartTime();
    	return taskMapper.listByFailedNotice(localDate.toString());
    }
    
    public List<Task> listBySuccessNotice() {
    	LocalDate localDate = getNoticeStartTime();
    	return taskMapper.listBySuccessNotice(localDate.toString());
    }
    
    public List<Task> listByRetryNotice() {
    	LocalDate localDate = getNoticeStartTime();
    	return taskMapper.listByRetryNotice(localDate.toString());
    }

	private LocalDate getNoticeStartTime() {
		LocalDate localDate = LocalDate.now().minusDays(NOTICE_DAY);
		return localDate;
	}


    /**
     * 根据Id修改Task信息
     * @param task
     */
    public void updateTaskInfo(Task task){
        Assert.notNull(task.getId());
        Task newTask = new Task();
        newTask.setId(task.getId());

        Map<String, Object> updateParam = Maps.newHashMap();
        if (task.getEndTime() != null){
            updateParam.put("end_time", task.getEndTime());
        }
        if (task.getExecTime() != null){
            updateParam.put("exec_time", task.getExecTime());
        }
        if (task.getStatus() != null){
            updateParam.put("status", task.getStatus());
        }

        if (task.getLogPath() != null){
            updateParam.put("log_path",task.getLogPath());
        }
        if (task.getExecuteDir() != null){
            updateParam.put("execute_dir",task.getExecuteDir());
        }
        if (task.getPid() != null){
        	updateParam.put("pid",task.getPid());
		}
		if (task.getIp() != null){
			updateParam.put("ip",task.getIp());
		}
        if (task.getEmrClusterId() != null){
        	updateParam.put("emr_cluster_id",task.getEmrClusterId());
		}

        newTask.setUpdateParam(updateParam);
        this.edit(newTask);
    }

    public Boolean checkIsStatus(Long id, TaskStatusEnum statusEnum) {
    	Task task = new Task();
    	task.setId(id);
    	task.setStatus(statusEnum);
    	Task retTask = this.get(task);
    	if (retTask != null) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public List<Task> listByTimeover(String date) {
    	Assert.notNull(date);
    	return taskMapper.listByTimeover(date, TaskStatusEnum.SCHEDULED);
    }


    public List<Task> listByAlarmDelay() {
    	LocalDate localDate = getNoticeStartTime();
    	return taskMapper.listByAlarmDelay(localDate.toString());
    }

    public List<TaskVO> listRunningTaskByHost(String host){
    	Task task = new Task();
    	task.setIp(host);
    	task.setStatus(TaskStatusEnum.RUNNING);
    	return this.list(task);
	}

	public List<Long> listByWaitValid() {
		Task task = new Task();
		task.setIsDeleted(false);
		task.setStatus(TaskStatusEnum.WAIT_VALID);
		List<TaskVO> tasks = this.list(task);
		if (CollectionUtils.isEmpty(tasks)) {
			return Lists.newArrayList();
		}
		return tasks.stream().map(TaskVO::getId).collect(Collectors.toList());
	}

    public List<Long> listByWaitValid(List<Long> ids) {
    	if (CollectionUtils.isEmpty(ids)) {
    		return Lists.newArrayList();
		}
		Task task = new Task();
    	task.setIsDeleted(false);
    	task.setIds(ids);
    	task.setStatus(TaskStatusEnum.WAIT_VALID);
    	List<TaskVO> tasks = this.list(task);
		if (CollectionUtils.isEmpty(tasks)) {
			return Lists.newArrayList();
		}
		return tasks.stream().map(TaskVO::getId).collect(Collectors.toList());
    }

	public List<Long> listDqcTasksByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Lists.newArrayList();
		}
		Task task = new Task();
		task.setIsDeleted(false);
		task.setIds(ids);
		task.setJobType(JobTypeEnum.DQC);
		List<TaskVO> tasks = this.list(task);
		if (CollectionUtils.isEmpty(tasks)) {
			return Lists.newArrayList();
		}
		return tasks.stream().map(TaskVO::getId).collect(Collectors.toList());
	}
}
