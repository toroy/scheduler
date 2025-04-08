package com.clubfactory.platform.scheduler.server.leader.runnable;

import com.clubfactory.platform.scheduler.core.vo.TaskDependsVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实例相关的dqc实例都跑成功了，可以将等待数据校验的实例设置为成功
 */
@Service
@Slf4j
public class DqcTaskRunnable extends SchedulerBaseService implements Runnable {

    @Override
    public void run() {
        super.init();
        // 没有等待校验的实例，返回
        List<Long> waitTaskIds = taskService.listByWaitValid();
        if (CollectionUtils.isEmpty(waitTaskIds)) {
            return;
        }
        // 等待校验的实例没有子节点，设置成功
        List<TaskDependsVO> vos = taskDependsService.listByParentIds(waitTaskIds);
        if (CollectionUtils.isEmpty(vos)) {
            taskService.updateSuccess(waitTaskIds);
            return;
        }
        // 等待校验的实例子节点没有dqc的节点，设置成功
        List<Long> taskIds = vos.stream().map(TaskDependsVO::getTaskId).collect(Collectors.toList());
        List<TaskVO> dqcTasks = taskService.listIdByDqcTask(taskIds);
        if (CollectionUtils.isEmpty(dqcTasks)) {
            taskService.updateSuccess(waitTaskIds);
            return;
        }
        List<Long> dqcTaskIds = dqcTasks.stream().map(TaskVO::getId).collect(Collectors.toList());
        Map<Long, Set<Long>> dependMap = vos.stream().filter(task -> dqcTaskIds.contains(task.getTaskId()))
                .collect(Collectors.groupingBy(TaskDependsVO::getParentId
                        , Collectors.mapping(TaskDependsVO::getTaskId, Collectors.toSet())));
        // 等待校验的实例子节点都成功了，设置成功
        List<Long> successIds = listSuccessIds(dqcTasks, dependMap);
        if (CollectionUtils.isNotEmpty(successIds)) {
            taskService.updateSuccess(successIds);
        }
        // 等待校验的实例子节点其中一个数据失败 或者 其中一个失败次数超过最大次数，设置失败
        List<Long> failedIds = listFailedIds(dqcTasks, dependMap);
        if (CollectionUtils.isNotEmpty(failedIds)) {
            taskService.updateDataFailed(failedIds);
        }
    }

    private List<Long> listSuccessIds(List<TaskVO> dqcTasks, Map<Long, Set<Long>> dependMap) {

        List<Long> successDqcTaskIds = dqcTasks.stream().filter(task -> TaskStatusEnum.SUCCESS == task.getStatus()).map(TaskVO::getId).collect(Collectors.toList());

        return dependMap.entrySet().stream().filter(child -> {
            for (Long taskId : child.getValue()) {
                if (!successDqcTaskIds.contains(taskId)) {
                    return false;
                }
            }
            return true;
        }).map(child -> child.getKey()).collect(Collectors.toList());
    }

    private List<Long> listFailedIds(List<TaskVO> dqcTasks, Map<Long, Set<Long>> dependMap) {
        List<Long> dataFailedDqcTaskIds = dqcTasks.stream().filter(task -> TaskStatusEnum.DATA_FAILED == task.getStatus())
                .map(TaskVO::getId).collect(Collectors.toList());

        List<Long> failedDqcTaskIds = dqcTasks.stream().filter(task -> TaskStatusEnum.FAILED == task.getStatus())
                .filter(task -> task.getRetryCount() >= task.getRetryMax())
                .map(TaskVO::getId).collect(Collectors.toList());

        List<Long> taskIds = Lists.newArrayList();
        taskIds.addAll(dataFailedDqcTaskIds);
        taskIds.addAll(failedDqcTaskIds);

        return dependMap.entrySet().stream().filter(child -> {
            for (Long taskId : child.getValue()) {
                if (taskIds.contains(taskId)) {
                    return true;
                }
            }
            return false;
        }).map(child -> child.getKey()).collect(Collectors.toList());
    }
}
