package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.GroupVO;
import com.clubfactory.platform.scheduler.dal.dao.GroupMapper;
import com.clubfactory.platform.scheduler.dal.po.Group;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class GroupService extends BaseNewService<GroupVO,Group> {

    @Resource
    GroupMapper groupMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(groupMapper);
    }

}
