package com.zhugeio.platform.scheduler.core.exception;

/**
 * @author xiejiajun
 */
public class BuildTaskFailedException extends RuntimeException{

    public BuildTaskFailedException(String message, Throwable cause){
        super(message,cause);
    }
}
