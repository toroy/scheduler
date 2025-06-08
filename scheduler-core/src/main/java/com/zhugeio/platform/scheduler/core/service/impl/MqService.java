package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.core.vo.MqVO;
import com.zhugeio.platform.scheduler.dal.dao.MqMapper;
import com.zhugeio.platform.scheduler.dal.po.Mq;
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
