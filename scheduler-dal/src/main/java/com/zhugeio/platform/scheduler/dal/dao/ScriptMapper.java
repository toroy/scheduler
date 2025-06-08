package com.zhugeio.platform.scheduler.dal.dao;

import com.zhugeio.platform.scheduler.dal.po.Script;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScriptMapper extends BaseMapper<Script>{


    Long selectIdByScriptName(@Param("scriptName") String scriptName);
}