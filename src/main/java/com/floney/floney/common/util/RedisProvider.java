package com.floney.floney.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisProvider {

    private final RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(
                key,
                value,
                timeout,
                TimeUnit.MILLISECONDS
        );
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
