package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.MacroVar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiejiajun
 */
@Mapper
public interface MacroVarMapper {


    /**
     * 新增配置
     * @param var
     * @return
     */
    int save(@Param("var") MacroVar var);

    /**
     * 更新配置
     * @param updateParam
     * @param updateTime
     * @param macroVar
     * @return
     */
    int update(@Param("updateParam") Map<String,Object> updateParam,
               @Param("updateTime") Date updateTime,
               MacroVar macroVar);

    /**
     * 删除配置
     * @param macroVar
     * @return
     */
    int delete(MacroVar macroVar);

    /**
     * 列出满足条件的配置
     * @param macroVar
     * @return
     */
    List<MacroVar> list(MacroVar macroVar);


    /**
     * 根据变量名称获取变量信息
     * @param varName
     * @return
     */
    MacroVar getByName(String varName);


}
