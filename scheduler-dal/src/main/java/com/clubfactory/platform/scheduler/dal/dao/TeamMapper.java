package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.Team;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamMapper extends BaseMapper<Team>{

    int truncate();
}