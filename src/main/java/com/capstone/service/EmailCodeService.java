package com.capstone.service;

import com.capstone.dto.SetEmailCode;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Duration;

public interface EmailCodeService extends UserDetailsService {
    void setValues(SetEmailCode setEmailCode);
    String getValues(String k);
}
