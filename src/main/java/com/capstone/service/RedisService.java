package com.capstone.service;

import java.time.Duration;

public interface RedisService {
    void setValues(String k, String v, Duration t);
    String getValues(String k);
}
