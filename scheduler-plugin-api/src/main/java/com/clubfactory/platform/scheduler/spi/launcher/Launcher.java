package com.clubfactory.platform.scheduler.spi.launcher;

import com.clubfactory.platform.scheduler.spi.exception.TaskException;

/**
 * @author xiejiajun
 */
public interface Launcher {

    /**
     * 启动任务
     * @param execCommand
     * @return
     * @throws TaskException
     */
    int launch(String execCommand) throws TaskException;


    /**
     * 取消任务
     * @throws Exception
     */
    void cancel() throws Exception;

    /**
     * 清理创建的资源
     * @return
     */
    boolean destroy();
}
