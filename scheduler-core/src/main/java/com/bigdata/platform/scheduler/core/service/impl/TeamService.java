package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.TeamVO;
import com.bigdata.platform.scheduler.dal.dao.TeamMapper;
import com.bigdata.platform.scheduler.dal.po.Team;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TeamService extends BaseNewService<TeamVO,Team> {

    @Resource
    TeamMapper teamMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(teamMapper);
    }

}
