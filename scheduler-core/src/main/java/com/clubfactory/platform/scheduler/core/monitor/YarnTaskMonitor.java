package com.clubfactory.platform.scheduler.core.monitor;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.service.impl.TaskMonitorService;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.po.TaskMonitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.clubfactory.platform.scheduler.common.Constants.YARN_RM_HA_HOSTS;
import static com.clubfactory.platform.scheduler.common.Constants.YARN_RM_HTTP_PORT;
import static com.clubfactory.platform.scheduler.core.utils.PropertyUtils.getString;

/**
 *  监控 yarn 应用的状态
 */
@Service
@Slf4j
public class YarnTaskMonitor {

    @Autowired
    private TaskMonitorService taskMonitorService;

    /**
     * @param taskVO
     * @param yarnAppId
     */
    public void monitor(TaskVO taskVO, String yarnAppId) throws Exception {
        this.monitor(taskVO, yarnAppId, null);
    }

    /**
     * @param taskVO
     * @param yarnAppId
     * @param failoverCnt
     * @throws Exception
     */
    public void monitor(TaskVO taskVO, String yarnAppId, Integer failoverCnt) throws Exception {
        this.monitor(taskVO,yarnAppId,null,null, failoverCnt);
    }

    /**
     * @param taskVO
     * @param yarnAppId
     * @param remoteRmHosts
     * @param failoverCnt
     */
    public void monitor(TaskVO taskVO, String yarnAppId,String remoteRmHosts, Integer failoverCnt) throws Exception {
        this.monitor(taskVO,yarnAppId,remoteRmHosts,null, failoverCnt);
    }

    /**
     * 提交监控
     *
     * @param taskVO
     * @param remoteRmHosts
     * @param restPort
     * @param yarnAppId yarn应用id，格式 application_1584463115284_6773
     * @param failoverCnt 允许的失败重启次数
     */
    public void monitor(TaskVO taskVO, String yarnAppId,String remoteRmHosts,String restPort, Integer failoverCnt) throws Exception {
        long taskId = taskVO.getId();
        failoverCnt = Objects.isNull(failoverCnt) ? Constants.DEFAULT_STREAMING_TASK_FAILOVER_RETRY_NUM : failoverCnt;
        remoteRmHosts = Optional.ofNullable(remoteRmHosts).orElse(getString(YARN_RM_HA_HOSTS));
        restPort = Optional.ofNullable(restPort).orElse(getString(YARN_RM_HTTP_PORT));
        log.info("add or update monitor for taskId:" + taskId + ", yarnAppId:" + yarnAppId +
                ", restPort:" + restPort + ", remoteRmHosts:" + remoteRmHosts);
        TaskMonitor existTm = taskMonitorService.getByTaskId(taskId);
        if (existTm == null) {
            TaskMonitor taskMonitor = new TaskMonitor();
            taskMonitor.setTaskId(taskVO.getId());
            taskMonitor.setYarnAppId(yarnAppId);
            taskMonitor.setRetryCounts(failoverCnt);
            taskMonitor.setMaxRetryNum(failoverCnt);
            taskMonitor.setRmHost(remoteRmHosts);
            taskMonitor.setRmPort(restPort);
            Date nowDate = new Date();
            taskMonitor.setCreateTime(nowDate);
            taskMonitor.setUpdateTime(nowDate);
            taskMonitorService.save(taskMonitor);
        } else {
            Map<String, Object> updateParam = Maps.newHashMap();
            updateParam.put("yarn_app_id", yarnAppId);
            updateParam.put("rm_host", remoteRmHosts);
            updateParam.put("rm_port", restPort);
            TaskMonitor toEditTm = new TaskMonitor();
            toEditTm.setId(existTm.getId());
            toEditTm.setUpdateParam(updateParam);
            taskMonitorService.edit(toEditTm);
        }
    }


}
