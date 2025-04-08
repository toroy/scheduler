package com.clubfactory.platform.scheduler.spi.param;

/**
 * Job参数接口
 * @author xiejiajun
 */
public interface IParameters {

    /**
     * 参数校验
     * @return
     */
    boolean checkParameters();

    /**
     * 归一化参数
     */
    default void normalizedParameter() {

    }

}
