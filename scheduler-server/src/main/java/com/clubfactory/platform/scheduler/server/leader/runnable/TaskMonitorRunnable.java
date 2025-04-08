package com.clubfactory.platform.scheduler.server.leader.runnable;


import com.alibaba.fastjson.JSONObject;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.service.impl.TaskMonitorService;
import com.clubfactory.platform.scheduler.core.service.impl.TaskService;
import com.clubfactory.platform.scheduler.core.utils.HttpUtils;
import com.clubfactory.platform.scheduler.core.utils.SysConfigUtil;
import com.clubfactory.platform.scheduler.core.vo.TaskMonitorVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.dal.po.TaskMonitor;
import com.clubfactory.platform.scheduler.server.constant.Constant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.clubfactory.platform.scheduler.common.Constants.*;

@Slf4j
@Service
public class TaskMonitorRunnable implements Runnable {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskMonitorService taskMonitorService;

    private ConcurrentHashMap<String, Long> appIdHttpTimeoutMap = new ConcurrentHashMap<>();

    private long timeoutTooLong = 1800L;//单位秒


    @Override
    public void run() {
        log.info("TaskMonitorRunnable running....");
        execute();
    }

    private void execute() {
        Integer checkDeplay = SysConfigUtil.getNumberByKey(Constant.TASK_MONITOR_PERIOD);

        List<TaskMonitorVO> allTms = taskMonitorService.list(new TaskMonitor());
        Map<Long, TaskStatusEnum> monitoredTaskStatus = this.getMonitoredTaskStatusMaps(allTms);
        for (TaskMonitorVO vo : allTms) {
            Long monitorId = vo.getId();
            String yarnAppId = vo.getYarnAppId();
            int retryCounts = vo.getRetryCounts();
            Long taskId = vo.getTaskId();
            String rmHosts = vo.getRmHost();
            String rmPort = vo.getRmPort();
            int maxRetryNum = Objects.isNull(vo.getMaxRetryNum()) || vo.getMaxRetryNum() <= 0
                    ? DEFAULT_STREAMING_TASK_FAILOVER_RETRY_NUM : vo.getMaxRetryNum();
            TaskStatusEnum currentTaskStatus = monitoredTaskStatus.get(taskId);

            // appId为空表示正在提交且新appId还未写入，这种情况不用重复检测
            if (StringUtils.isBlank(yarnAppId)) {
                log.info("task {} appId is not ready, skip state check", taskId);
                continue;
            }

            if (!this.isRequireRecoverStatus(currentTaskStatus)) {
                log.info("task {} status is {} , don't need to recover again", taskId, currentTaskStatus);
                continue;
            }
            if (this.isIgnorableTask(currentTaskStatus)) {
                log.info("task {} status is {}, don't never need to recover, remove it's monitor info", taskId, currentTaskStatus);
                this.removeTMonitor(monitorId);
                continue;
            }
            log.info("start to monitor yarnAppId:" + yarnAppId + ", taskId:" + taskId +
                    ", rmPort:" + rmPort + ", rmHosts:" + rmHosts);

            if (!checkTaskIdYarnIdBinding(taskId, yarnAppId)) {
                continue;
            }
            try {
                TaskStatusEnum appStatus = getEmrYarnAppStatus(yarnAppId, rmHosts, rmPort);

                if (appStatus == null) {
                    continue;
                }
                if (appStatus == TaskStatusEnum.FAILED) {
                    if (retryCounts > 0) {
                        log.info("taskId:" + taskId + " retry(retryCounts:" + retryCounts + ") when failed");
                        updateTaskInfo(monitorId, taskId, TaskStatusEnum.INIT);
                        // 重试次数减1的同时appId置空，防止重复提交
                        reduceRetryCounts(vo, true);

                    } else {
                        log.info("taskId:" + taskId + " failed");
                        updateTaskInfo(monitorId, taskId, appStatus);
                        removeTMonitor(monitorId);
                    }

                } else if (appStatus == TaskStatusEnum.KILLED || appStatus == TaskStatusEnum.KILLING) {
                    log.info("taskId:" + taskId + " " + appStatus.getDesc() + " and retry(retryCounts:" + retryCounts + ")");
                    if (retryCounts > 0) {
                        reduceRetryCounts(vo);
                    } else {
                        checkTaskRunThenKillIt(monitorId, taskId);
                        removeTMonitor(monitorId);
                    }

                } else if (appStatus != TaskStatusEnum.RUNNING) {
                    log.info("taskId:" + taskId + " " + appStatus.getDesc() + " and retry(retryCounts:" + retryCounts + ")");
                    if (retryCounts > 0) {
                        reduceRetryCounts(vo);
                    } else {
                        removeTMonitor(monitorId);
                    }
                } else {
                    // appStatus is RUNNING, reset the retryCounts
                    if (retryCounts < maxRetryNum) {
                        this.resetRetryCounts(monitorId, maxRetryNum);
                    }
                }
                appIdHttpTimeoutMap.remove(yarnAppId);

            } catch (Exception e) {
                if (Constants.HTTP_ERR.equalsIgnoreCase(e.getMessage())) {
                    long httpTimeout = appIdHttpTimeoutMap.computeIfAbsent(yarnAppId, k -> 0L);
                    if (httpTimeout >= timeoutTooLong) {
                        if (TaskStatusEnum.KILLED != currentTaskStatus) {
                            updateTaskInfo(monitorId, taskId, TaskStatusEnum.FAILED);
                        }
                        removeTMonitor(monitorId);
                        log.warn("taskId:" + taskId + " timeout too long, deal as failed");
                        appIdHttpTimeoutMap.remove(yarnAppId);
                    } else {
                        log.warn("taskId:" + taskId + " timeout");
                        appIdHttpTimeoutMap.put(yarnAppId, httpTimeout + checkDeplay);
                    }
                }
                log.error("monitor taskId:" + taskId, e);
            }
        }
    }

    /**
     * 检查是否是需要恢复的状态
     * @param status
     * @return
     */
    private boolean isRequireRecoverStatus(TaskStatusEnum status) {
        return TaskStatusEnum.INIT != status && TaskStatusEnum.READY != status && TaskStatusEnum.SCHEDULED != status;
    }

    /**
     * 是否为不再需要监控的任务状态
     * @param taskStatus
     * @return
     */
    private boolean isIgnorableTask(TaskStatusEnum taskStatus) {
        if (Objects.isNull(taskStatus)) {
            return true;
        }
        return TaskStatusEnum.KILLED == taskStatus || TaskStatusEnum.KILLING == taskStatus
                || TaskStatusEnum.MANUAL_SUCCESS == taskStatus || TaskStatusEnum.SUCCESS == taskStatus;

    }

    /**
     * 获取当前被监控的任务的状态信息
     * @param monitorInfos
     * @return
     */
    private Map<Long, TaskStatusEnum> getMonitoredTaskStatusMaps(List<TaskMonitorVO> monitorInfos) {
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return Maps.newHashMap();
        }
        List<Long> taskIds = monitorInfos.stream().filter(info -> Objects.nonNull(info.getTaskId()))
                .map(TaskMonitorVO::getTaskId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskIds)) {
            return Maps.newHashMap();
        }
        Task taskFilter = new Task();
        taskFilter.setIsDeleted(false);
        taskFilter.setIds(taskIds);
        taskFilter.setQueryListFieldName("id");
        List<TaskVO> taskVOList = this.taskService.list(taskFilter);
        if (CollectionUtils.isEmpty(taskVOList)) {
            return Maps.newHashMap();
        }
        Map<Long, TaskStatusEnum> taskStatusMap = Maps.newHashMap();
        taskVOList.stream().filter(task -> Objects.nonNull(task.getId()))
                .forEach(info -> taskStatusMap.put(info.getId(), info.getStatus()));
        return taskStatusMap;
    }

    private boolean checkTaskIdYarnIdBinding(Long taskId, String yarnAppId) {
        TaskMonitor tm = new TaskMonitor();
        tm.setTaskId(taskId);
        tm.setYarnAppId(yarnAppId);
        TaskMonitor po = taskMonitorService.get(tm);
        if (po== null || po.getId() == null) {
            log.warn("----- can't find taskId:" + taskId + " binding with yarnAppId:" + yarnAppId);
            return false;
        } else {
            return true;
        }
    }

    private void reduceRetryCounts(TaskMonitorVO vo, boolean resetAppId) {
        int newRetryCounts = vo.getRetryCounts() - 1;
        TaskMonitorVO newVo = new TaskMonitorVO();
        newVo.setId(vo.getId());
        Map<String, Object> updateParam = Maps.newHashMap();
        updateParam.put("retry_counts", newRetryCounts);
        if (resetAppId) {
            updateParam.put("yarn_app_id", "");
        }
        newVo.setUpdateParam(updateParam);
        taskMonitorService.edit(newVo);
    }

    private void reduceRetryCounts(TaskMonitorVO vo) {
        this.reduceRetryCounts(vo, false);
    }

    private void resetRetryCounts(Long monitorId, int maxRetryNum) {
        TaskMonitor taskMonitor = new TaskMonitor();
        taskMonitor.setId(monitorId);
        Map<String, Object> updateParams = Maps.newHashMap();
        updateParams.put("retry_counts", maxRetryNum);
        updateParams.put("max_retry_num", maxRetryNum);
        taskMonitor.setUpdateParam(updateParams);
        this.taskMonitorService.edit(taskMonitor);
    }

    private void updateTaskInfo(Long monitorId, Long taskId, TaskStatusEnum appStatus) {
        Task newTask = new Task();
        newTask.setId(taskId);
        newTask.setStatus(appStatus);
        taskService.updateTaskInfo(newTask);
    }

    private void checkTaskRunThenKillIt(Long monitorId, Long taskId) {
        Task taskPo = taskService.get(taskId);
        if (taskPo != null) {
            Task newTask = new Task();
            newTask.setId(taskId);
            newTask.setStatus(TaskStatusEnum.KILLED);
            taskService.updateTaskInfo(newTask);
        }
    }

    private void removeTMonitor(Long monitorId) {
        taskMonitorService.removeByIds(null, Lists.newArrayList(monitorId));
    }


    /**
     * accept 等yarn状态也被认为是 running
     */
    public TaskStatusEnum getEmrYarnAppStatus(
            String yarnAppId, String rmHosts, String rmPort) throws Exception {
        if (StringUtils.isBlank(yarnAppId)) {
            log.error("yarnAppId is blank");
            return null;
        }
        rmPort = StringUtils.isNotBlank(rmPort) ? rmPort : "8088";

        String[] rmHostArr = rmHosts.split(",");
        for (String rmHost : rmHostArr) {
            try {
                String stateApiUrl = String.format("http://%s:%s/ws/v1/cluster/apps/", rmHost, rmPort) + yarnAppId;
                TaskStatusEnum tse = getEmrYarnRespStatus(yarnAppId, stateApiUrl);
                if (tse != null) {
                    return tse;
                }
            } catch (Exception e) {
                if (e.getMessage().equalsIgnoreCase(HTTP_ERR)) {
                    throw e;
                } else {
                    log.error("", e);
                }
            }
        }
        return null;
    }

    private TaskStatusEnum getEmrYarnRespStatus(String yarnAppId, String stateApiUrl) throws Exception {
        String responseContent = HttpUtils.get(stateApiUrl);
        if (responseContent == null) {
            throw new Exception(HTTP_ERR);
        }

        //{"state":"FINISHED"}
        JSONObject respJObj = JSONObject.parseObject(responseContent);
        JSONObject appJObj = respJObj.getJSONObject("app");
        String state = null;
        String finalStatus = null;
        if (appJObj == null) {
            throw new Exception("yarnAppId:" + yarnAppId + " can't parse yarn response: " + responseContent);
        } else {
            state = appJObj.getString("state");
            finalStatus = appJObj.getString("finalStatus");
        }
        if (state != null) {
            switch (state.toUpperCase()) {
                case SUCCEEDED:
                    return TaskStatusEnum.SUCCESS;
                case FAILED:
                    return TaskStatusEnum.FAILED;
                case KILLED:
                    return TaskStatusEnum.KILLED;
                case FINISHED:
                    if ("FAILED".equalsIgnoreCase(finalStatus)) {
                        return TaskStatusEnum.FAILED;
                    } else {
                        return TaskStatusEnum.SUCCESS;
                    }
                default:
                    return TaskStatusEnum.RUNNING;
            }
        } else {
            log.error("yarnAppId:" + yarnAppId + " state is null, responseContent: " + responseContent);
            return null;
        }
    }


}
