package com.zhugeio.platform.scheduler.engine.dao;

import com.zhugeio.platform.scheduler.core.service.ICommonService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author xiejiajun
 */
public class DataAccessFactory {

    /**
     * @return 构建数据访问代理
     */
    public static ICommonService getDataAccessProxy(){
        ICommonService dataAccessWrapper = DataAccessWrapper.getInstance();
        InvocationHandler dataAccessHandler = new DataAccessProxy<>(dataAccessWrapper);
        return (ICommonService) Proxy.newProxyInstance(ICommonService.class.getClassLoader(),
                new Class<?>[]{ICommonService.class},dataAccessHandler);
    }
}
