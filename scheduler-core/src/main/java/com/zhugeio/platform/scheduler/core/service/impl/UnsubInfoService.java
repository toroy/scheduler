package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.core.vo.UnsubInfoVO;
import com.zhugeio.platform.scheduler.dal.dao.UnsubInfoMapper;
import com.zhugeio.platform.scheduler.dal.po.UnsubInfo;
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
