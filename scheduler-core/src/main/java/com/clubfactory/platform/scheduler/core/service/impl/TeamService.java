package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.TeamVO;
import com.clubfactory.platform.scheduler.dal.dao.TeamMapper;
import com.clubfactory.platform.scheduler.dal.po.Team;
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
