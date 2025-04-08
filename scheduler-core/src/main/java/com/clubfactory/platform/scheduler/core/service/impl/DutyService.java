package com.clubfactory.platform.scheduler.core.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.DutyVO;
import com.clubfactory.platform.scheduler.dal.dao.DutyMapper;
import com.clubfactory.platform.scheduler.dal.po.Duty;

@Service
public class DutyService extends BaseNewService<DutyVO,Duty> {

    @Resource
    DutyMapper dutyMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(dutyMapper);
    }

}
