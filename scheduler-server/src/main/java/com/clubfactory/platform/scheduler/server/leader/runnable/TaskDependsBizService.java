package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.common.utils.TaskDependsUtil;
import com.clubfactory.platform.scheduler.core.service.impl.JobOnlineService;
import com.clubfactory.platform.scheduler.core.service.impl.TaskDependsService;
import com.clubfactory.platform.scheduler.core.service.impl.TaskService;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineDependsVO;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.dal.po.JobOnline;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.dal.po.TaskDepends;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskDependsBizService {

	@Resource
	TaskDependsService taskDependsService;
	@Resource
	JobOnlineService jobOnlineService;
	@Resource
	TaskService taskService;
	
	public List<TaskDepends> genSelfDepends(List<JobOnline> jobs, List<Task> tasks) {
		// 通过过滤创建时间 < 当前时间 所有已存在的实例
		List<Task> parentTasks = taskService.listMaxTask();
		Map<Long, List<Task>>  parentTasksMap = parentTasks.stream().collect(Collectors.groupingBy(Task::getJobId));
		
		Map<Long, List<Task>>  tasksMap = tasks.stream().collect(Collectors.groupingBy(Task::getJobId));
		List<TaskDepends> taskDepends = Lists.newArrayList();
		for (JobOnline jobOnline : jobs) {
			try {
				List<TaskDepends> subTaskDepends = TaskDependsUtil.genSelfDependsByJob(jobOnline, tasksMap.get(jobOnline.getJobId()), parentTasksMap.get(jobOnline.getJobId()));
				taskDepends.addAll(subTaskDepends);
			} catch (Exception e) {
				log.error("genSelfDependsByJobId,jobId:{}", jobOnline.getJobId(), e);
			}
			
		}
		return taskDepends;
	}

	public List<TaskDepends> genSameDepends(List<JobOnlineVO> jobOnlines, List<JobOnlineDependsVO> jobOnlineDepends, List<Task> tasks, Date date) {
		List<TaskDepends> taskDepends = Lists.newArrayList();
		Map<Long, List<JobOnlineDependsVO>> dependMap = jobOnlineDepends.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getJobId));
		Map<Long, List<Task>> taskMap = tasks.stream().collect(Collectors.groupingBy(Task::getJobId));
		Map<Long, List<Task>> tasksMapByWeek = taskService.getSameTaskMapByWeek(date);
		Map<Long, List<Task>> tasksMapByMonth = taskService.getSameTaskMapByMonth(date);
		Map<Long, JobOnlineVO> jobMap = getJobOnline(jobOnlines);
		for (JobOnline jobOnline : jobOnlines) {
			List<JobOnlineDependsVO> dependsVOs = dependMap.get(jobOnline.getJobId());
			if (CollectionUtils.isEmpty(dependsVOs)) {
				continue;
			}
			for (JobOnlineDependsVO jobOnlineDependsVO : dependsVOs) {
				Long jobId = jobOnlineDependsVO.getJobId();
				Long parentId = jobOnlineDependsVO.getParentId();
				List<Task> tasksByJobId = taskMap.get(jobId);
				if (CollectionUtils.isEmpty(tasksByJobId)) {
					continue;
				}
				List<Task> allParentTasks = TaskDependsUtil.mergeTaskById(taskMap.get(parentId), tasksMapByWeek.get(parentId), tasksMapByMonth.get(parentId));
				try {
					JobOnlineVO parentJobOnline = getParentJobOnline(jobMap, parentId);
					List<TaskDepends> subTaskDepends = TaskDependsUtil.genSameDependsByCycleType(jobMap.get(jobId), parentJobOnline, tasksByJobId, allParentTasks);
					taskDepends.addAll(subTaskDepends);
				} catch (Exception e) {
					log.error("genSameDependsByCycleType,jobId:{},parentId:{}", jobId, parentId, e);
				}
			}
		}
		return taskDepends;
	}

	public List<TaskDepends> genPrevDepends(List<JobOnlineVO> jobOnlines, List<JobOnlineDependsVO> jobOnlineDepends, List<Task> tasks, Date date) {
		List<TaskDepends> taskDepends = Lists.newArrayList();
		Map<Long, List<JobOnlineDependsVO>> dependMap = jobOnlineDepends.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getJobId));
		Map<Long, List<Task>> taskMap = tasks.stream().collect(Collectors.groupingBy(Task::getJobId));
		// TODO 现在只支持天任务的上一周期，也就是依赖昨天的实例
		Map<Long, List<Task>> yesterdayTasksMap = taskService.getYesterdayTaskMap(date);
		Map<Long, JobOnlineVO> jobMap = getJobOnline(jobOnlines);
		for (JobOnline jobOnline : jobOnlines) {
			List<JobOnlineDependsVO> dependsVOs = dependMap.get(jobOnline.getJobId());
			if (CollectionUtils.isEmpty(dependsVOs)) {
				continue;
			}
			for (JobOnlineDependsVO jobOnlineDependsVO : dependsVOs) {
				Long jobId = jobOnlineDependsVO.getJobId();
				Long parentId = jobOnlineDependsVO.getParentId();
				List<Task> tasksByJobId = taskMap.get(jobId);
				if (CollectionUtils.isEmpty(tasksByJobId)) {
					continue;
				}
				List<Task> allParentTasks = yesterdayTasksMap.get(parentId);
				try {
					JobOnlineVO parentJobOnline = getParentJobOnline(jobMap, parentId);
					List<TaskDepends> subTaskDepends = TaskDependsUtil.genPrevDependsByCycleType(jobMap.get(jobId), parentJobOnline, tasksByJobId, allParentTasks);
					taskDepends.addAll(subTaskDepends);
				} catch (Exception e) {
					log.error("genPrevDependsByCycleType,jobId:{},parentId:{}", jobId, parentId, e);
				}
			}
		}
		return taskDepends;
	}
	

	public List<TaskDepends> genAllDepends(List<JobOnlineVO> jobOnlines, List<JobOnlineDependsVO> jobOnlineDepends, List<Task> tasks, Date date) {
		List<TaskDepends> taskDepends = Lists.newArrayList();
		Map<Long, List<JobOnlineDependsVO>> dependMap = jobOnlineDepends.stream().collect(Collectors.groupingBy(JobOnlineDependsVO::getJobId));
 		Map<Long, List<Task>> taskMap = tasks.stream().collect(Collectors.groupingBy(Task::getJobId));
		Map<Long, JobOnlineVO> jobMap = getJobOnline(jobOnlines);
		Map<Long, List<Task>> tasksMapByWeek = taskService.getAllTaskMapByWeek(date);
		Map<Long, List<Task>> tasksMapByMonth = taskService.getAllTaskMapByMonth(date);
		for (JobOnlineVO jobOnline : jobOnlines) {
			List<JobOnlineDependsVO> dependsVOs = dependMap.get(jobOnline.getJobId());
			if (CollectionUtils.isEmpty(dependsVOs)) {
				continue;
			}
			for (JobOnlineDependsVO jobOnlineDependsVO : dependsVOs) {
				Long jobId = jobOnlineDependsVO.getJobId();
				Long parentId = jobOnlineDependsVO.getParentId();
				List<Task> tasksByJobId = taskMap.get(jobId);
				if (CollectionUtils.isEmpty(tasksByJobId)) {
					continue;
				}
				List<Task> allParentTasks = TaskDependsUtil.mergeTaskById(taskMap.get(parentId), tasksMapByWeek.get(parentId), tasksMapByMonth.get(parentId));
				try {
					JobOnlineVO parentJobOnline = getParentJobOnline(jobMap, parentId);
					List<TaskDepends> subTaskDepends = TaskDependsUtil.genAllDependsByCycleType(jobMap.get(jobId), parentJobOnline, tasksByJobId, allParentTasks);
					taskDepends.addAll(subTaskDepends);
				} catch (Exception e) {
					log.error("genAllDependsByCycleType,jobId:{},parentId:{}", jobId, parentId, e);
				}
				
			}
		}
		return taskDepends;
	}

	private JobOnlineVO getParentJobOnline(Map<Long, JobOnlineVO> jobMap, Long parentId) {
		JobOnlineVO parentJobOnline = jobMap.get(parentId);
		// 马上跑的才有这个场景，量少，直接查
		if (parentJobOnline == null) {
			parentJobOnline = (JobOnlineVO)jobOnlineService.getByJobId(parentId);
		}
		return parentJobOnline;
	}
	
	private Map<Long, JobOnlineVO> getJobOnline(List<JobOnlineVO> jobOnlines) {
		if (CollectionUtils.isEmpty(jobOnlines)) {
			return Maps.newHashMap();
		}
		Map<Long, JobOnlineVO> map = Maps.newHashMap();
		for (JobOnlineVO jobOnline : jobOnlines) {
			map.put(jobOnline.getJobId(), jobOnline);
		}
		return map;
	}

	

	
}
