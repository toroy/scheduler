package com.clubfactory.platform.scheduler.core.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.CollectDbVO;
import com.clubfactory.platform.scheduler.dal.dao.CollectDbMapper;
import com.clubfactory.platform.scheduler.dal.po.CollectDb;

@Service
public class CollectDbService extends BaseNewService<CollectDbVO,CollectDb> {

    @Resource
    CollectDbMapper collectDbMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(collectDbMapper);
    }
    
}
