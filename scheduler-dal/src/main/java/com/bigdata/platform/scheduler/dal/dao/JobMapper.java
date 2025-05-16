package com.bigdata.platform.scheduler.dal.dao;

import java.util.List;

import com.bigdata.platform.scheduler.dal.po.Job;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobMapper extends BaseMapper<Job>{

	List<Job> listByName(Job job);
}