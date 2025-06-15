package com.zhugeio.platform.scheduler.dal.dao;

import com.zhugeio.platform.scheduler.dal.po.FileParam;
import com.zhugeio.platform.scheduler.dal.po.Script;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileParamMapper extends BaseMapper<FileParam>{


    Long selectIdByScriptName(@Param("FileParamName") String fileParamName);
}