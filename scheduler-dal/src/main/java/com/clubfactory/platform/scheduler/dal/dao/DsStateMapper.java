package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.DsState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xiejiajun
 */
@Mapper
public interface DsStateMapper {

    /**
     * 保存测试结果
     * @param dsState
     * @return
     */
    int save(@Param("dsState") DsState dsState);

    /**
     * 批量保存测试结果
     * @param list
     * @return
     */
    int saveBatch(List<DsState> list);

    /**
     * 根据WorkerIp清除对应测试记录
     * @param workerIp
     * @return
     */
    int deleteByWorkerIp(@Param("workerIp") String workerIp);


    /**
     * 查询满足条件的信息
     * @param dsState
     * @return
     */
    List<DsState> list(DsState dsState);

    /**
     * getById
     * @param id
     * @return
     */
    DsState select(@Param("id") Long id);
}
