package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.GroupInfoVO;
import com.bigdata.platform.scheduler.dal.dao.GroupInfoMapper;
import com.bigdata.platform.scheduler.dal.po.GroupInfo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class GroupInfoService extends BaseNewService<GroupInfoVO,GroupInfo> {

    @Resource
    GroupInfoMapper groupInfoMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(groupInfoMapper);
    }
    
}
