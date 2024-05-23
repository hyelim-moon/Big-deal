package com.capstone.entity;

import com.capstone.dto.EmailCodeDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.descriptor.java.DurationJavaType;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Date;

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
    public EmailCodeDetails details() {
        return new EmailCodeDetails(email, code, time);
    }

}
