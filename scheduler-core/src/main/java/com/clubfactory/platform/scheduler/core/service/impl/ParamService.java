package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.ParamVO;
import com.clubfactory.platform.scheduler.dal.dao.ParamMapper;
import com.clubfactory.platform.scheduler.dal.po.Param;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class ParamService extends BaseNewService<ParamVO,Param> {

    @Resource
    ParamMapper paramMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(paramMapper);
    }

}
