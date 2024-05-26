package com.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;

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
    public static Collection<? extends  GrantedAuthority> authorities() {
        return Stream.of("ROLE_NEW").map(SimpleGrantedAuthority::new).toList();
    }

}
