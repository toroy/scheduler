package com.zhugeio.platform.scheduler.core.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.zhugeio.platform.scheduler.core.vo.CollectDbVO;
import org.springframework.stereotype.Service;

import com.zhugeio.platform.scheduler.dal.dao.CollectDbMapper;
import com.zhugeio.platform.scheduler.dal.po.CollectDb;

@Service
public class CollectDbService extends BaseNewService<CollectDbVO,CollectDb> {

    @Resource
    CollectDbMapper collectDbMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(collectDbMapper);
    }
    
}
