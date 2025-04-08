package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.LogMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xiejiajun
 */
@Mapper
public interface LogMapMapper {

    /**
     * 保存日志映射关系
     * @param logMap
     * @return
     */
    int save(@Param("logMap") LogMap logMap);


    /**
     * 查询taskId对应的所有日志映射关系
     * @param taskId
     * @return
     */
    List<LogMap> list(@Param("taskId") Long taskId);

    /**
     * 根据日志ID查询映射信息
     * @param logId
     * @return
     */
    LogMap select(@Param("logId") Long logId);
}
