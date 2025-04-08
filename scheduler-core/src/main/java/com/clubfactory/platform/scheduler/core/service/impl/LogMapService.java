package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.dal.dao.LogMapMapper;
import com.clubfactory.platform.scheduler.dal.po.LogMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiejiajun
 */
@Service
public class LogMapService {

    @Resource
    private LogMapMapper logMapMapper;

    public int save(LogMap logMap){
        Assert.notNull(logMap,"logMap");
        Assert.notNull(logMap.getTaskId(),"taskId");
        Assert.notNull(logMap.getLogHost(),"日志host");
        Assert.notNull(logMap.getLogName(),"日志名称");
        Assert.notNull(logMap.getLogPath(),"日志路径");
        return logMapMapper.save(logMap);
    }

    public List<LogMap> list(Long taskId){
        Assert.notNull(taskId,"taskId");
        return logMapMapper.list(taskId);
    }
}
