package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.DemoVO;
import com.clubfactory.platform.scheduler.dal.dao.DemoMapper;
import com.clubfactory.platform.scheduler.dal.po.Demo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class DemoService extends BaseNewService<DemoVO,Demo> {

    @Resource
    DemoMapper demoMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(demoMapper);
    }

}
