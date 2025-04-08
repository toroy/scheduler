package com.clubfactory.platform.scheduler.core.proxy;

import com.clubfactory.boot.autoconfigure.redis.ClubRedisFactory;
import com.clubfactory.boot.config.ClubConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2022/6/22 2:30 下午
 **/
@Service
public class LettuceConfig {

    @Primary
    @Bean("redisTemplate")
    @ClubConfigurationProperties("club-boot.redis.gaia")
    public StringRedisTemplate primaryRedis() {
        return ClubRedisFactory.createStringTemplate();
    }
}
