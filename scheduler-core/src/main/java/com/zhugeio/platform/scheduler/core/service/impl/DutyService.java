package com.zhugeio.platform.scheduler.core.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.zhugeio.platform.scheduler.core.vo.DutyVO;
import org.springframework.stereotype.Service;

import com.zhugeio.platform.scheduler.dal.dao.DutyMapper;
import com.zhugeio.platform.scheduler.dal.po.Duty;

@Service
public class DutyService extends BaseNewService<DutyVO,Duty> {

    @Resource
    DutyMapper dutyMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(dutyMapper);
    }

}
