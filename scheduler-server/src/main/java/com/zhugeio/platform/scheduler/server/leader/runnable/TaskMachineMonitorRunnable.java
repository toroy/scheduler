package com.zhugeio.platform.scheduler.server.leader.runnable;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhugeio.platform.scheduler.core.service.impl.JobOnlineService;
import com.zhugeio.platform.scheduler.core.service.impl.JobService;
import com.zhugeio.platform.scheduler.core.service.impl.TaskService;
import com.zhugeio.platform.scheduler.core.vo.JobOnlineVO;
import com.zhugeio.platform.scheduler.core.vo.JobVO;
import com.zhugeio.platform.scheduler.core.vo.TaskVO;
import com.zhugeio.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.zhugeio.platform.scheduler.dal.enums.TaskStatusEnum;
import com.zhugeio.platform.scheduler.dal.po.Task;
import com.zhugeio.platform.scheduler.server.dto.ParamDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;


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
    @Autowired
    private JobOnlineService jobOnlineService;
    @Autowired
    private JobService jobService;

    private static final int RETRY_NUM = 5;
    private static final int TIME_OUT_MS = 5000;
    private static final int DEFAULT_PORT = 80;
    private static final String DEFAULT_NAME = "port";

    @Override
    public void run() {
        Task taskFilter = new Task();
        taskFilter.setIsDeleted(false);
        taskFilter.setStatus(TaskStatusEnum.RUNNING);
        taskFilter.setCycleType(JobCycleTypeEnum.REAL_TIME);
        taskFilter.setType("JAVA_STREAMING");
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
        final Map<Long, String> allMap = getAllMap(taskVOList);

        List<Long> ids = Lists.newArrayList();
        int port = DEFAULT_PORT;
        for (TaskVO taskVO : taskVOList) {
            boolean isHealth = false;
            int timeoutMs = TIME_OUT_MS;
            for (int i = 0; i < RETRY_NUM; i++) {
                port = getPort(allMap.get(taskVO.getJobId()), port);
                isHealth = checkServiceTcpHealth(taskVO.getIp(), port, timeoutMs);
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

    private Map<Long, String> getAllMap(List<TaskVO> taskVOList) {
        Map<Long, String> allMap = Maps.newHashMap();
        List<Long> jobOnlineIds = taskVOList.stream()
                .filter(task -> !task.getIsTemp())
                .map(TaskVO::getJobId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(jobOnlineIds)) {
            List<JobOnlineVO> jobOnlineVOS = jobOnlineService.listByIds(jobOnlineIds);
            Map<Long, String> jobOnlineParamMap = jobOnlineVOS.stream().collect(Collectors.toMap(JobOnlineVO::getId, JobOnlineVO::getParams));
            allMap.putAll(jobOnlineParamMap);
        }

        List<Long> jobIds = taskVOList.stream()
                .filter(Task::getIsTemp)
                .map(TaskVO::getJobId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(jobIds)) {
            List<JobVO> jobVOS = jobService.listByJobId(jobIds);
            Map<Long, String> jobParamMap = jobVOS.stream().collect(Collectors.toMap(JobVO::getId, JobVO::getParams));
            allMap.putAll(jobParamMap);
        }
        return allMap;
    }

    private int getPort(String paramStr, int port) {
        if (StringUtils.isBlank(paramStr)) {
            return port;
        }
        List<ParamDto> paramDtos = JSON.parseArray(paramStr, ParamDto.class);
        Map<String, String> paramDtoMap = paramDtos.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ParamDto::getName, ParamDto::getValue
                        , (s1, s2) -> s2));
        if (MapUtils.isNotEmpty(paramDtoMap)) {
            String portStr = paramDtoMap.get(DEFAULT_NAME);
            if (StringUtils.isNotBlank(portStr)) {
                try {
                    port = Integer.parseInt(portStr);
                } catch (Exception e) {
                    log.error("TCP健康检查参数转换失败: portStr: {}, error: {}",portStr, e.getMessage());
                }
            }
        }
        return port;
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
