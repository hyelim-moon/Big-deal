package com.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringBootCapstoneApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootCapstoneApplication.class, args);
    }
}
