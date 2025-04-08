package com.clubfactory.platform.scheduler.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clubfactory.platform.scheduler.dal.po.Job;

@Mapper
public interface JobMapper extends BaseMapper<Job>{

	List<Job> listByName(Job job);
}