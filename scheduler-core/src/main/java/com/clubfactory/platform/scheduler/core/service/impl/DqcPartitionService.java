package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.DqcPartitionVO;
import com.clubfactory.platform.scheduler.dal.dao.DqcPartitionMapper;
import com.clubfactory.platform.scheduler.dal.po.DqcPartition;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class DqcPartitionService extends BaseNewService<DqcPartitionVO,DqcPartition> {

    @Resource
    DqcPartitionMapper dqcPartitionMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(dqcPartitionMapper);
    }

}
