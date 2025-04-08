package com.clubfactory.platform.scheduler.server.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2022/9/2 11:37 上午
 **/
public class GuavaCacheUtil {

    /**
     * 单个缓存最大值
     */
    private static int MAXIMUM_SIZE = 10_000;

    /**
     * 缓存过期时间，单位秒
     */
    private static int EXPIRE_TIME_MINUTES = 12 * 60;

    private static Cache<Object, Optional<Object>> CACHE;

    static {
        CACHE = CacheBuilder.newBuilder().refreshAfterWrite(EXPIRE_TIME_MINUTES, TimeUnit.MINUTES)
                .maximumSize(MAXIMUM_SIZE).build(new CacheLoader<Object, Optional<Object>>() {
                    @Override
                    public Optional<Object> load(Object key) throws Exception {
                        return null;
                    }
                });
    }

    public static void put(Object key, Object val) {
        CACHE.put(key, Optional.ofNullable(val));
    }


    public static void clear(Object key) {
        CACHE.invalidate(key);
    }

    public static Object get(String key, Callable<? extends Optional<Object>> valueLoader) {
        try {
            Optional<Object> opt = CACHE.get(key, valueLoader);
            return opt.isPresent() ? opt.get() : null;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T get(Object key) {
        try {
            Optional<Object> opt = CACHE.get(key, () -> Optional.ofNullable(null));
            if (opt.isPresent()) {
                return (T) opt.get();
            } else {
                return null;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;

    }

}