package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.core.vo.GroupInfoVO;
import com.zhugeio.platform.scheduler.dal.dao.GroupInfoMapper;
import com.zhugeio.platform.scheduler.dal.po.GroupInfo;
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
