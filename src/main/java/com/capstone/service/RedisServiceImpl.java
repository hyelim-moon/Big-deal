package com.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void setValues(String k, String v, Duration t) {
        redisTemplate.opsForValue().set(k, v, t);
    }
    @Override
    public String getValues(String k) {
        return (String) redisTemplate.opsForValue().get(k);
    }
}
