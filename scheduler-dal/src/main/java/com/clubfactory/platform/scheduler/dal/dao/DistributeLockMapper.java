package com.clubfactory.platform.scheduler.dal.dao;


import com.clubfactory.platform.scheduler.dal.po.DistributeLock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author xiejiajun
 */
@Mapper
public interface DistributeLockMapper {


    /**
     * 尝试获取锁
     * @param lockInfo
     * @return 影响行数
     */
    int lock(@Param("lockInfo") DistributeLock lockInfo);


    /**
     * 用于检查锁是否已经被其他应用占用，若检测到已占用就直接放弃竞争操作
     * @param lockInfo
     * @return
     */
    DistributeLock selectLock(@Param("lockInfo") DistributeLock lockInfo);


    /**
     * 释放锁
     * @param resourceId
     */
    void unlock(@Param("resourceId") Integer resourceId);

    /**
     * 释放程序异常退出未释放或者超时未释放的死锁
     * @param currentTime
     */
    void clearDeadLock(@Param("currentTime") Long currentTime);

}
