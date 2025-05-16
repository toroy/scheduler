package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.UnsubInfoVO;
import com.bigdata.platform.scheduler.dal.dao.UnsubInfoMapper;
import com.bigdata.platform.scheduler.dal.po.UnsubInfo;
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
