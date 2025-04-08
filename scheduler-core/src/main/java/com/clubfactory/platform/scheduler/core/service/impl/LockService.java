package com.clubfactory.platform.scheduler.core.service.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.common.enums.ResourceType;
import com.clubfactory.platform.scheduler.dal.dao.DistributeLockMapper;
import com.clubfactory.platform.scheduler.dal.po.DistributeLock;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiejiajun
 */
@Slf4j
@Service
public class LockService {

    @Resource
    private DistributeLockMapper distributeLockMapper;

    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init(){
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleWithFixedDelay(this::clearExpireLock,5, 5, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy(){
        if (this.executorService != null){
            this.executorService.shutdownNow();
        }
    }

    private void clearExpireLock(){
        this.distributeLockMapper.clearDeadLock(System.currentTimeMillis());
    }


    /**
     * 获取锁
     * @param resourceType：锁类别
     * @param maxHoldTime：持有锁的最长时间，单位: s
     * @return
     */
    public boolean tryLock(ResourceType resourceType, int maxHoldTime) {
        DistributeLock lockInfo = new DistributeLock();
        lockInfo.setRemark(resourceType.getDesc());
        lockInfo.setResourceId(resourceType.getResourceId());
        return tryLock(maxHoldTime,10,lockInfo);
    }

    /**
     * @param resourceType: 锁类别
     * @param maxHoldTime：持有锁的最长时间，单位: s
     * @param timeout: 等待锁超时时间，单位: s
     * @return
     */
    public boolean tryLock(ResourceType resourceType, int maxHoldTime, int timeout) {
        DistributeLock lockInfo = new DistributeLock();
        lockInfo.setRemark(resourceType.getDesc());
        lockInfo.setResourceId(resourceType.getResourceId());
        return tryLock(maxHoldTime,timeout,lockInfo);
    }

    /**
     * 获取锁，只尝试一次
     * @param resourceType
     * @return
     */
    public boolean tryLockOnce(ResourceType resourceType) {
        return tryLockOnce(resourceType,60);
    }

    /**
     * 获取锁，只尝试一次
     * @param resourceType
     * @param maxHoldTime
     * @return
     */
    public boolean tryLockOnce(ResourceType resourceType,int maxHoldTime ) {
        DistributeLock lockInfo = new DistributeLock();
        lockInfo.setRemark(resourceType.getDesc());
        lockInfo.setResourceId(resourceType.getResourceId());
        if (distributeLockMapper.selectLock(lockInfo) != null) {
            return false;
        }
        try {
            lockInfo.setExpireTime(System.currentTimeMillis() + maxHoldTime * 1000);
            int effectRow = distributeLockMapper.lock(lockInfo);
            if (effectRow == 1) {
                return true;
            }
        }catch (Exception e){
            log.error("Thread {} 获取锁失败",Thread.currentThread().getName());
        }
        return false;
    }



    private boolean tryLock(int maxHoldTime,int timeout,DistributeLock lockInfo) {
        long maxHoldSec = maxHoldTime * 1000;
        long secCount = 0;
        while (true){
            try {
                if (distributeLockMapper.selectLock(lockInfo) != null) {
                    if (++secCount >= timeout){
                        return false;
                    }
                    Thread.sleep(1000);
                    continue;
                }
                lockInfo.setExpireTime(System.currentTimeMillis() + maxHoldSec );
                int effectRow = -1;
                try {
                    effectRow = distributeLockMapper.lock(lockInfo);
                }catch (Exception e){
                    log.info("SQL执行出错：{}",e.getMessage());
                }
                if (effectRow == 1) {
                    return true;
                }
                if (++secCount >= timeout){
                    return false;
                }
                Thread.sleep(1000);
            } catch (Exception e ) {
                log.error("Thread {} 获取锁失败",Thread.currentThread().getName());
            }

        }
    }

    /**
     * 释放锁
     * @param resourceType
     */
    public void unlock(ResourceType resourceType){
        distributeLockMapper.unlock(resourceType.getResourceId());
    }


}
