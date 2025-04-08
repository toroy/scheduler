package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.UnsubInfoVO;
import com.clubfactory.platform.scheduler.dal.dao.UnsubInfoMapper;
import com.clubfactory.platform.scheduler.dal.po.UnsubInfo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class UnsubInfoService extends BaseNewService<UnsubInfoVO,UnsubInfo> {

    @Resource
    UnsubInfoMapper unsubInfoMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(unsubInfoMapper);
    }

}
