package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.DqcTaskVO;
import com.clubfactory.platform.scheduler.dal.dao.DqcTaskMapper;
import com.clubfactory.platform.scheduler.dal.po.DqcTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class DqcTaskService extends BaseNewService<DqcTaskVO,DqcTask> {

    @Resource
    DqcTaskMapper dqcTaskMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(dqcTaskMapper);
    }

}
