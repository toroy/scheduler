package com.clubfactory.platform.scheduler.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clubfactory.platform.scheduler.dal.po.JobOnline;

@Mapper
public interface JobOnlineMapper extends BaseMapper<JobOnline>{

	List<JobOnline> listByName(JobOnline jobOnline);

}