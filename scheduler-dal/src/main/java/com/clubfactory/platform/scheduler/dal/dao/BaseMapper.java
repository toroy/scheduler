package com.clubfactory.platform.scheduler.dal.dao;



import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.clubfactory.platform.scheduler.dal.po.BasePO;


/**
 * @param <P>
 */
public interface BaseMapper<P extends BasePO> {

    
    int save(@Param("po") P po);

    int remove(P po);

    int logicRemove(P po);

    int edit(P po);

    P get(P po);

    List<P> list(P po);

    int count(P po);

    int saveBatch(List<P> list);
}
