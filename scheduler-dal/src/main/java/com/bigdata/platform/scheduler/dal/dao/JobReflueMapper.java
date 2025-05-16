package com.bigdata.platform.scheduler.dal.dao;

import com.bigdata.platform.scheduler.dal.po.JobReflue;
import com.bigdata.platform.scheduler.dal.dto.SubscribeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobReflueMapper extends BaseMapper<JobReflue>{


    /**
     * 根据JobId列表查询订阅所需信息
     * @param jobIds
     * @return
     */
    List<SubscribeDto> listSubscribeInfos(@Param("jobIds") List<Long> jobIds);
}