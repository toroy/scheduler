package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiejiajun
 */
@Mapper
public interface SysConfigMapper {

    /**
     * 新增配置
     * @param sysConfig
     * @return
     */
    int save(@Param("sysConf")SysConfig sysConfig);

    /**
     * 更新配置
     * @param sysConfig
     * @return
     */
    int update(@Param("updateParam") Map<String,Object> updateParam,
               @Param("updateTime") Date updateTime,
               SysConfig sysConfig);

    /**
     * 删除配置
     * @param sysConfig
     * @return
     */
    int delete(SysConfig sysConfig);

    /**
     * 列出满足条件的配置
     * @param sysConfig
     * @return
     */
    List<SysConfig> list(SysConfig sysConfig);
    
    /**
     * 获取配置
     * 
     * @param sysConfig
     * @return
     */
    SysConfig get(SysConfig sysConfig);


    /**
     * 列出满足条件的配置
     * @param sysConfig
     * @return
     */
    List<SysConfig> listValid(SysConfig sysConfig);
}
