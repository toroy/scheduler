package com.clubfactory.platform.scheduler.engine.supplier;

/**
 * @author xiejiajun
 */
@FunctionalInterface
public interface PluginSupplier<T> {

    /**
     * 使用给定的类加载器创建对象
     *
     * @param cl 类加载器
     * @return 实例化的对象
     * @throws Exception NoSuchMethodException SecurityException
     */
    T get(ClassLoader cl) throws Exception;
}
