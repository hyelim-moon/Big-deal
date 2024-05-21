package com.capstone.service;

import com.capstone.dto.SetEmailCode;
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
    public void setValues(SetEmailCode setEmailCode) {
        redisTemplate.opsForValue().set(setEmailCode.getEmail(), setEmailCode.getCode(), setEmailCode.getTime());
    }
    @Override
    public String getValues(String k) {
        return (String) redisTemplate.opsForValue().get(k);
    }
}
