package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.dto.SubscribeDto;
import com.clubfactory.platform.scheduler.dal.po.Job;
import com.clubfactory.platform.scheduler.dal.po.JobCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobCollectMapper extends BaseMapper<JobCollect>{

    /**
     * 根据JobId列表查询订阅所需信息
     * @param jobIds
     * @return
     */
    List<SubscribeDto> listSubscribeInfos(@Param("jobIds") List<Long> jobIds);
}