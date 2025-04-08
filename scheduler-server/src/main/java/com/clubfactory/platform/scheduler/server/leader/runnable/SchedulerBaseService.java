package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.clubfactory.platform.scheduler.core.service.impl.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.utils.SpringBean;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.core.vo.ScriptVO;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.server.alarm.IMNoticeService;
import com.clubfactory.platform.scheduler.server.leader.ZkAbstractService;
import com.google.common.collect.Maps;

@Service
public class SchedulerBaseService extends ZkAbstractService {
	
	MachineService machineService;
	ScriptService scriptService;
	JobOnlineService jobOnlineService;
	JobService jobService;
	JobCollectService jobCollectService;
	TaskService taskService;
	JobOnlineDependsService jobOnlineDependsService;
	TaskDependsService taskDependsService;
	TaskDependsBizService taskDependsBizService;
	IMNoticeService noticeBizService;
	DqcRuleService dqcRuleService;
	
	public void init() {
		if (scriptService == null) {
			scriptService = SpringBean.getBean(ScriptService.class);
		}
		if (dqcRuleService == null) {
			dqcRuleService = SpringBean.getBean(DqcRuleService.class);
		}
		if (taskDependsBizService == null) {
			taskDependsBizService = SpringBean.getBean(TaskDependsBizService.class);
		}
		if (jobCollectService == null) {
			jobCollectService = SpringBean.getBean(JobCollectService.class);
		}
		if (jobOnlineService == null) {
			jobOnlineService = SpringBean.getBean(JobOnlineService.class);
		}
		if (taskService == null) {
			taskService = SpringBean.getBean(TaskService.class);
		}
		if (machineService == null) {
			machineService = SpringBean.getBean(MachineService.class);
		}
		if (jobOnlineDependsService == null) {
			jobOnlineDependsService = SpringBean.getBean(JobOnlineDependsService.class);
		}
		if (taskDependsService == null) {
			taskDependsService = SpringBean.getBean(TaskDependsService.class);
		}
		if (jobService == null) {
			jobService = SpringBean.getBean(JobService.class);
		}
		if (noticeBizService == null) {
			noticeBizService = SpringBean.getBean(IMNoticeService.class);
		}
		
	}
	
	protected Map<Long, String> getFileNameMap(List<Task> tasks) {
		List<Long> scriptIds = tasks.stream().map(Task::getScriptId).distinct().collect(Collectors.toList());
		return scriptService.getScriptMap(scriptIds);
	}
	
	protected List<ScriptVO> listScript(List<Task> tasks) {
		List<Long> scriptIds = tasks.stream().map(Task::getScriptId).distinct().collect(Collectors.toList());
		return scriptService.listByIds(scriptIds);
	}
	
	/**
	 * 正式节点取jobOnline的version
	 * 临时节点取script的version	
	 * @param tasks
	 * @return
	 */
	public Map<Long, Integer> getVersionMap(List<Task> tasks) {
		Map<Long, Integer> versionMap = Maps.newHashMap();
		List<Task> notTempTasks = tasks.stream().filter(task -> task.getIsTemp().equals(false)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(notTempTasks)) {
			List<Long> jobIds = notTempTasks.stream().map(Task::getJobId).distinct().collect(Collectors.toList());
			List<JobOnlineVO> jobOnlines = jobOnlineService.listAllByIds(jobIds);
			Map<Long, Integer> jobVersionMap = jobOnlines.stream()
					.filter(vo -> vo.getVersion() != null)
					.collect(Collectors.toMap(JobOnlineVO::getJobId, JobOnlineVO::getVersion, (oldvalue, newValue) -> newValue));
			for (Task task : notTempTasks) {
				if (jobVersionMap.get(task.getJobId()) == null) {
					continue;
				}
				versionMap.put(task.getId(), jobVersionMap.get(task.getJobId()));
			}
			
		}
		
		List<Task> tempTasks = tasks.stream().filter(task -> task.getIsTemp().equals(true)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(tempTasks)) {
			List<Long> scriptIds = tempTasks.stream().map(Task::getScriptId).distinct().collect(Collectors.toList());
			List<ScriptVO> scriptVOs = scriptService.listByIds(scriptIds);
			Map<Long, Integer> scriptVersionMap = scriptVOs.stream().collect(Collectors.toMap(ScriptVO::getId, ScriptVO::getVersion));
			for (Task task : tempTasks) {
				if (scriptVersionMap.get(task.getScriptId()) == null) {
					continue;
				}
				versionMap.put(task.getId(), scriptVersionMap.get(task.getScriptId()));
			}
		}
		return versionMap;
	}
	

}
