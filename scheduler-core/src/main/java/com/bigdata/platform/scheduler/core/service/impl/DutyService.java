package com.bigdata.platform.scheduler.core.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bigdata.platform.scheduler.core.vo.DutyVO;
import com.bigdata.platform.scheduler.dal.dao.DutyMapper;
import com.bigdata.platform.scheduler.dal.po.Duty;

@Service
public class DutyService extends BaseNewService<DutyVO,Duty> {

    @Resource
    DutyMapper dutyMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(dutyMapper);
    }

}
