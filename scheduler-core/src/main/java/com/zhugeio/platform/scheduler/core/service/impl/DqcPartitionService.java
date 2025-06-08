package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.core.vo.DqcPartitionVO;
import com.zhugeio.platform.scheduler.dal.dao.DqcPartitionMapper;
import com.zhugeio.platform.scheduler.dal.po.DqcPartition;
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
