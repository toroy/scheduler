package com.clubfactory.platform.scheduler.engine.utils;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.invoker.SafeInvoker;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 基于ZK的任务锁
 *
 * @author xiejiajun
 */
@Slf4j
public class TaskLock {
    private final static String TASK_LOCK_RELEASED = "instance must be started before calling this method";

    private final String taskLockParentPath;
    private final InterProcessMutex mutex;
    private final CuratorFramework zkClient;

    public TaskLock(CuratorFramework zkClient, Long taskId) {
        this.zkClient = zkClient;
        this.taskLockParentPath = String.format("%s/task_lock_%s", Constants.DEFAULT_TASK_LOCK_ROOT_PATH, taskId);
        this.mutex = new InterProcessMutex(zkClient, taskLockParentPath);

    }

    /**
     * 获取任务锁
     *
     * @param timeout: 超时时间，单位(s)
     * @return : 获取成功返回true,反之false
     */
    public boolean tryLock(int timeout) {
        return BooleanUtils.isTrue(
                SafeInvoker.call(() -> this.mutex.acquire(timeout, TimeUnit.SECONDS), e -> log.error(e.getMessage(), e))
        );
    }

    /**
     * 释放锁
     *
     * @param deleteLockPath
     */
    public void unlock(boolean deleteLockPath) {
        if (this.mutex != null && this.mutex.isAcquiredInThisProcess()) {
            try {
                this.mutex.release();
                log.debug("lock release success ...");
            } catch (Exception e) {
                if (TASK_LOCK_RELEASED.equals(e.getMessage())) {
                    log.debug("lock release success ...");
                } else {
                    log.error("lock release failed : " + e.getMessage(), e);
                }
            } finally {
                if (deleteLockPath) {
                    SafeInvoker.invoke(() -> {
                        // 清理当前lock父节点
                        List<String> children = this.zkClient.getChildren().forPath(taskLockParentPath);
                        if (CollectionUtils.isEmpty(children)) {
                            this.zkClient.delete().forPath(taskLockParentPath);
                        }
                    }, e -> log.error("删除Task Lock Node {} 失败: {}", taskLockParentPath, e.getMessage()));
                }
            }
        }

    }

    /**
     * 释放锁时顺便尝试删除锁的父路径
     */
    public void unlock() {
        this.unlock(true);
    }

    /**
     * 强制清除TaskLock及其父路径
     */
    public boolean forceClear() {
        Boolean deleted = SafeInvoker.call(() -> {
            Boolean taskLockPathExists = SafeInvoker.call(
                    () -> this.zkClient.checkExists().forPath(taskLockParentPath) != null, (Consumer<Exception>) null);
            if (BooleanUtils.isTrue(taskLockPathExists)) {
                this.zkClient.delete().deletingChildrenIfNeeded().forPath(taskLockParentPath);
            }
            log.info("{} deleted task lock {} from zk success" , OSUtils.getHost(), taskLockParentPath);
            return true;
        }, e -> log.error(e.getMessage(), e));
        return BooleanUtils.isTrue(deleted);
    }
}
