package com.clubfactory.platform.scheduler.common.invoker;

/**
 * @author xiejiajun
 */
public interface Runner {

    /**
     * 业务逻辑入口
     * @throws Exception
     */
    void run() throws Exception;
}
