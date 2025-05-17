package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.DqcPartitionVO;
import com.bigdata.platform.scheduler.dal.dao.DqcPartitionMapper;
import com.bigdata.platform.scheduler.dal.po.DqcPartition;
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
