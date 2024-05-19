package com.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service("emailCodeServiceImpl")
@RequiredArgsConstructor
public class EmailCodeServiceImpl implements EmailCodeService {
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
