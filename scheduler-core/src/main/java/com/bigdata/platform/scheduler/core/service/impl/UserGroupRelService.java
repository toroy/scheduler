package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.UserGroupRelVO;
import com.bigdata.platform.scheduler.dal.dao.UserGroupRelMapper;
import com.bigdata.platform.scheduler.dal.po.UserGroupRel;
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
