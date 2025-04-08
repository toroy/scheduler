package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.DqcTableVO;
import com.clubfactory.platform.scheduler.dal.dao.DqcTableMapper;
import com.clubfactory.platform.scheduler.dal.po.DqcTable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class DqcTableService extends BaseNewService<DqcTableVO,DqcTable> {

    @Resource
    DqcTableMapper dqcTableMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(dqcTableMapper);
    }

}
