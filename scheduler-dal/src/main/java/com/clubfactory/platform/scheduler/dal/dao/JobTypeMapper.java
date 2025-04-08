package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.JobType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xiejiajun
 */
@Mapper
public interface JobTypeMapper {

    /**
     * 保存任务实现
     * @param jobType
     * @return
     */
    int save(@Param("jobType") JobType jobType);


    /**
     * 获取JobType列表
     * @return
     */
    List<JobType> list();

    /**
     * 根据ID查询插件信息
     * @param typeId
     * @return
     */
     JobType select(@Param("typeId") Long typeId);

    /**
     * 根据ID查询插件信息
     * @param pluginName
     * @param category
     * @return
     */
    JobType selectByName(@Param("pluginName") String pluginName, @Param("category") String category);
}
