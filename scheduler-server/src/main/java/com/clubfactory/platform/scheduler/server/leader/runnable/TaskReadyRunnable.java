package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.TaskDependsVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 实例置为ready
 *
 * @author zhoulijiang
 * @date 2019-12-12 11:04
 *
 */
@Service
@Slf4j
public class TaskReadyRunnable extends SchedulerBaseService implements Runnable {

	/**
	 * 没有父依赖的，置为ready
	 * 有父依赖，且父依赖为success的
	 */
	@Override
	public void run() {
		super.init();
		
		// 获取满足开始时间的所有task实例
		Date nowDate = new Date();
		List<TaskVO> taskVos = taskService.listIdsByStartTask(nowDate);
		if (CollectionUtils.isEmpty(taskVos)) {
			return;
		}
		
		// 获取task实例对应的依赖信息
		List<Long> ids = listTaskIds(taskVos);
		List<TaskDependsVO> dependVos = taskDependsService.listByIds(ids);
		// 找出没有父节点的依赖
		List<Long> noParentReadyIds = this.listNoParentIds(dependVos, ids);
		if (CollectionUtils.isNotEmpty(noParentReadyIds)) {
			taskService.updateReady(noParentReadyIds);
		}
		// 找出有节点依赖的task
		List<Long> hasParentReadyIds = this.listHasParentIds(dependVos, ids);
		if (CollectionUtils.isNotEmpty(hasParentReadyIds)) {
			// 这些task的父节点都成功
			List<Long> readIds = listReadyIds(dependVos, hasParentReadyIds);
			// 找出DQC实例的父节点为等待验证状态的
			List<Long> dqcReadIds = listDqcReadyIds(dependVos, hasParentReadyIds);
			if (CollectionUtils.isNotEmpty(dqcReadIds)) {
				readIds.addAll(dqcReadIds);
			}
			if (CollectionUtils.isNotEmpty(readIds)) {
				taskService.updateReady(readIds);
			}
		}
	}

	/**
	 * 获取父节点为等待数据校验，子节点为dqc的实例
	 *
	 * @param dependVos 节点依赖对象
	 * @param hasParentReadyIds 父节点ready的节点
	 * @return List<Long>：要执行的节点id列表
	 */
	private List<Long> listDqcReadyIds(List<TaskDependsVO> dependVos, List<Long> hasParentReadyIds) {
		List<Long> parentIds = dependVos.stream().map(TaskDependsVO::getParentId).collect(Collectors.toList());
		List<Long> parentWaitIds = taskService.listByWaitValid(parentIds);
		List<Long> dqcTaskIds = taskService.listDqcTasksByIds(hasParentReadyIds);
		return dependVos.stream().filter(dependVo -> parentWaitIds.contains(dependVo.getParentId()))
				.filter(dependVo -> dqcTaskIds.contains(dependVo.getTaskId()))
				.map(TaskDependsVO::getTaskId).collect(Collectors.toList());
	}

	/*
	 * 通过检查，依赖中父节点全成功的，返回
	 */
	private List<Long> listReadyIds(List<TaskDependsVO> dependVos, List<Long> readyIds) {
		// 获取所有成功的父节点信息
		List<Long> parentIds = dependVos.stream().map(TaskDependsVO::getParentId).collect(Collectors.toList());
		List<Long> parentSucceeIds = taskService.listBySuccess(parentIds);
		Map<Long, List<TaskDependsVO>> taskMap = dependVos.stream().collect(Collectors.groupingBy(TaskDependsVO::getTaskId));
		
		// 找出所有父节点成功信息的数据，并更新ready
		List<Long> taskIds = Lists.newArrayList();
		for (Entry<Long, List<TaskDependsVO>> entry : taskMap.entrySet()) {
			// 不在给予的范围内，过滤
			if (!readyIds.contains(entry.getKey())) {
				continue;
			}
			Boolean isSuccess = true;
			List<TaskDependsVO> dependsVOs = entry.getValue();
			// 如果有一个父节点没有成功，则状态为false
			for (TaskDependsVO dependsVO : dependsVOs) {
				if (!parentSucceeIds.contains(dependsVO.getParentId())) {
					isSuccess = false;
				}
			}
			if (isSuccess) {
				taskIds.add(entry.getKey());
			}
		}
		return taskIds;
	}

	private List<Long> listTaskIds(List<TaskVO> taskVos) {
		List<Long> ids = taskVos.stream()
				.map(Task::getId)
				.collect(Collectors.toList());
		return ids;
	}
	
    private List<Long> listNoParentIds(List<TaskDependsVO> dependVos, List<Long> ids) {
    	if (CollectionUtils.isEmpty(dependVos)) {
    		return ids;
    	}
    	List<Long> taskIds = dependVos.stream().map(TaskDependsVO::getTaskId).collect(Collectors.toList());
    	return ids.stream().filter(id -> !taskIds.contains(id)).collect(Collectors.toList());
    }
    
    private List<Long> listHasParentIds(List<TaskDependsVO> dependVos, List<Long> ids) {
    	if (CollectionUtils.isEmpty(dependVos)) {
    		return ids;
    	}
    	List<Long> taskIds = dependVos.stream().map(TaskDependsVO::getTaskId).collect(Collectors.toList());
    	return ids.stream().filter(id -> taskIds.contains(id)).collect(Collectors.toList());
    }
}
