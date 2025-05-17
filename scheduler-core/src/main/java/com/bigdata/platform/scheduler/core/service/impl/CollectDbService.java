package com.bigdata.platform.scheduler.core.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bigdata.platform.scheduler.core.vo.CollectDbVO;
import com.bigdata.platform.scheduler.dal.dao.CollectDbMapper;
import com.bigdata.platform.scheduler.dal.po.CollectDb;

@Service
public class CollectDbService extends BaseNewService<CollectDbVO,CollectDb> {

    @Resource
    CollectDbMapper collectDbMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(collectDbMapper);
    }
    
}
