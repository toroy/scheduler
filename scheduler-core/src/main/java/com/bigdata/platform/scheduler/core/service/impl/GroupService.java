package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.GroupVO;
import com.bigdata.platform.scheduler.dal.dao.GroupMapper;
import com.bigdata.platform.scheduler.dal.po.Group;
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
