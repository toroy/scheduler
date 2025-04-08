package com.clubfactory.platform.scheduler.core.exception;

/**
 * @author xiejiajun
 */
public class PluginLoadException extends RuntimeException {

    public PluginLoadException(String errorMsg, Throwable e) {
        super(errorMsg, e);
    }

    public PluginLoadException(String errorMsg) {
        super(errorMsg);
    }
}
