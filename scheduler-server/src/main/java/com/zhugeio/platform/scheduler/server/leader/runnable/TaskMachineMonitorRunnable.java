package com.zhugeio.platform.scheduler.server.leader.runnable;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zhugeio.platform.scheduler.core.service.impl.TaskService;
import com.zhugeio.platform.scheduler.core.vo.TaskVO;
import com.zhugeio.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.zhugeio.platform.scheduler.dal.enums.TaskStatusEnum;
import com.zhugeio.platform.scheduler.dal.po.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Objects;


/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2025/8/31 00:21
 **/
@Slf4j
@Service
public class TaskMachineMonitorRunnable implements Runnable {

    @Autowired
    private TaskService taskService;

    private static final int RETRY_NUM = 5;
    private static final int TIME_OUT_MS = 5000;

    @Override
    public void run() {
        Task taskFilter = new Task();
        taskFilter.setIsDeleted(false);
        taskFilter.setStatus(TaskStatusEnum.RUNNING);
        taskFilter.setCycleType(JobCycleTypeEnum.REAL_TIME);
        taskFilter.setType("javaStreaming");
        List<TaskVO> taskVOList = taskService.list(taskFilter);
        if (CollectionUtils.isEmpty(taskVOList)) {
            return;
        }
        final List<Long> ids = getInitList(taskVOList);
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        taskService.updateInit(ids);
        log.info("javaStreaming init: {}", JSON.toJSONString(ids));
    }

    private List<Long> getInitList(List<TaskVO> taskVOList) {
        List<Long> ids = Lists.newArrayList();
        for (TaskVO taskVO : taskVOList) {
            boolean isHealth = false;
            int timeoutMs = TIME_OUT_MS;
            for (int i = 0; i < RETRY_NUM; i++) {
                isHealth = checkServiceTcpHealth(taskVO.getIp(), taskVO.getMachineId().intValue(), timeoutMs);
                if (isHealth) {
                    break;
                }
                timeoutMs = timeoutMs * 2;
            }
            if (!isHealth) {
                ids.add(taskVO.getId());
            }
        }
        return ids;
    }


    /**
     * TCP Socket健康检查
     * @param host 主机地址
     * @param port 端口号
     * @param timeoutMs 超时时间(毫秒)
     * @return true-服务可达，false-服务不可达
     */
    private boolean checkServiceTcpHealth(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return socket.isConnected();
        } catch (IOException e) {
            log.warn("TCP健康检查失败: {}:{}, 错误: {}", host, port, e.getMessage());
            return false;
        }
    }
}
