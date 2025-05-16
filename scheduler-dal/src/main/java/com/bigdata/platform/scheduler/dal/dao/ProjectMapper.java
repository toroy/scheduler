package com.bigdata.platform.scheduler.dal.dao;

import com.bigdata.platform.scheduler.dal.po.Project;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper extends BaseMapper<Project>{


    List<String> getProjectNames();
}