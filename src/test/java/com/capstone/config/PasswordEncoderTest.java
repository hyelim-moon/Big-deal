package com.capstone.config;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {
    private PasswordEncoder passwordEncoder;
    @Test
    public void encode() {
        passwordEncoder = new BCryptPasswordEncoder();
        String password = "1234";
        String encodePassword = passwordEncoder.encode(password);
        System.out.println(encodePassword);
        Assertions.assertTrue(passwordEncoder.matches(password, encodePassword));
    }
}
