package com.capstone.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Duration;

public interface EmailCodeService {
    void setValues(String k, String v, Duration t);
    String getValues(String k);
}
