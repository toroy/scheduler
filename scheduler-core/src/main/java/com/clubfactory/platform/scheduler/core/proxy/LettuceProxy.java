package com.clubfactory.platform.scheduler.core.proxy;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2022/6/22 2:30 下午
 **/
@Service
public class LettuceProxy {

    @Resource
    private StringRedisTemplate redisTemplate;

    public Boolean setIfAbsent(String key, String value, Integer dur) {
        ValueOperations operations = getOperations();
        return operations.setIfAbsent(key, value, Duration.ofMinutes(dur));
    }

    public Object get(String key) {
        ValueOperations operations = getOperations();
        return operations.get(key);
    }

    public String get(String key, String defaultValue) {
        ValueOperations operations = getOperations();
        Object object = operations.get(key);
        if (object == null) {
            return defaultValue;
        }
        return object.toString();
    }

    private ValueOperations<String, String> getOperations() {
        return redisTemplate.opsForValue();
    }
}
