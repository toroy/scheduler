package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.TaskMonitorVO;
import com.clubfactory.platform.scheduler.dal.dao.TaskMonitorMapper;
import com.clubfactory.platform.scheduler.dal.po.TaskMonitor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TaskMonitorService extends BaseNewService<TaskMonitorVO,TaskMonitor> {

    @Resource
    TaskMonitorMapper taskMonitorMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(taskMonitorMapper);
    }

    /**
     * 根据taskId获取Monitor信息
     * @param taskId
     * @return
     */
    public TaskMonitor getByTaskId(Long taskId){
        TaskMonitor taskMonitor = new TaskMonitor();
        taskMonitor.setTaskId(taskId);
        return this.get(taskMonitor);
    }

}
