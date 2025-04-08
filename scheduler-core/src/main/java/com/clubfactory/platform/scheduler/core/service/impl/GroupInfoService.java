package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.GroupInfoVO;
import com.clubfactory.platform.scheduler.dal.dao.GroupInfoMapper;
import com.clubfactory.platform.scheduler.dal.po.GroupInfo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Service
public class GroupInfoService extends BaseNewService<GroupInfoVO,GroupInfo> {

    @Resource
    GroupInfoMapper groupInfoMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(groupInfoMapper);
    }
    
}
