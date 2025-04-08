package com.clubfactory.platform.scheduler.engine.dao;

import com.clubfactory.platform.scheduler.core.service.ICommonService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author xiejiajun
 */
public class DataAccessProxy<T extends ICommonService> implements InvocationHandler {

    private T target;

    public DataAccessProxy(T target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        target.preInvoke();
        return method.invoke(target, args);
    }
}
