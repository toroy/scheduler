package com.clubfactory.platform.scheduler.core.thread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xiejiajun
 * 全局服务停止信号
 */
public class Stopper {

    private static volatile AtomicBoolean signal = new AtomicBoolean(false);

    public static final boolean isStoped(){
        return signal.get();
    }

    public static final boolean isRunning(){
        return !signal.get();
    }

    public static final void stop(){
        signal.getAndSet(true);
    }
}
