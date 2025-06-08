package com.zhugeio.platform.scheduler.dal.dao;

import java.util.List;

import com.zhugeio.platform.scheduler.dal.po.JobOnline;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobOnlineMapper extends BaseMapper<JobOnline>{

	List<JobOnline> listByName(JobOnline jobOnline);

}