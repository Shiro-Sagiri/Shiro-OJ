package com.shiro.sojbackendcommon.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeJwt(Long userId, String jwt) {
        redisTemplate.opsForValue().set(String.valueOf(userId), jwt, 2, TimeUnit.HOURS);
    }

    public void deleteJwt(Long userId) {
        redisTemplate.delete(String.valueOf(userId));
    }
}
