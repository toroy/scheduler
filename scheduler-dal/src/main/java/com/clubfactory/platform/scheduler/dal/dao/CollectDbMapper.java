package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.CollectDb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CollectDbMapper extends BaseMapper<CollectDb>{

    /**
     * 主要用于获取指定数据源名称对应的数据源
     * @param dsName
     * @return
     */
    Long  selectIdByDsName(@Param("dsName") String dsName);

    /**
     * 主要用于获取指定数据源名称对应的数据源
     * @param dsName
     * @return
     */
    CollectDb selectByDsName(@Param("dsName") String dsName);
}