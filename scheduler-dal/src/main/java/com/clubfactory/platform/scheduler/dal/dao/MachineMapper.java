package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.Machine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MachineMapper extends BaseMapper<Machine>{

    /**
     * 根据IP统计对应worker的slots数量
     * @param ip
     * @return
     */
    Integer countSlotsByIp(@Param("ip") String ip);
}