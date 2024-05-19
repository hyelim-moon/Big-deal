package com.capstone.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Profile("dev")
@Configuration
public class DevRedisConfig {
    @Value("${spring.redis.port}")
    private String port;
    @Autowired
    private RedisServer server;
    @PostConstruct
    public void serverStart() {
        server.start();
    }
    @PreDestroy
    public void serverStop() {
        server.stop();
    }
}
