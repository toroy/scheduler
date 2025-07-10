package com.zhugeio.platform.scheduler.dal.dao;

import java.util.List;

import com.zhugeio.platform.scheduler.dal.po.Job;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobMapper extends BaseMapper<Job>{

	List<Job> listByName(Job job);

	List<Job> listHasParamFiles();
}