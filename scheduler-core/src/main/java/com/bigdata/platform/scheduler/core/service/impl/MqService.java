package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.MqVO;
import com.bigdata.platform.scheduler.dal.dao.MqMapper;
import com.bigdata.platform.scheduler.dal.po.Mq;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class MqService extends BaseNewService<MqVO,Mq> {

    @Resource
    MqMapper mqMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(mqMapper);
    }

}
