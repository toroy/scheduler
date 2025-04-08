package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.UserGroupRelVO;
import com.clubfactory.platform.scheduler.dal.dao.UserGroupRelMapper;
import com.clubfactory.platform.scheduler.dal.po.UserGroupRel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class UserGroupRelService extends BaseNewService<UserGroupRelVO,UserGroupRel> {

    @Resource
    UserGroupRelMapper userGroupRelMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(userGroupRelMapper);
    }

}
