package com.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

//@Profile("dev")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailCode {
    @Id
    private String email;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private Duration time;
    public void overwrite(String code, Duration time) {
        this.code = code;
        this.time = time;
    }
}
