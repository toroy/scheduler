package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.dto.SubscribeDto;
import com.clubfactory.platform.scheduler.dal.po.TableOnlineLineage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TableOnlineLineageMapper extends BaseMapper<TableOnlineLineage>{


    /**
     * 根据Id列表查询订阅所需信息
     * @param idList
     * @return
     */
    List<SubscribeDto> listSubscribeInfos(@Param("idList") List<Long> idList);
}