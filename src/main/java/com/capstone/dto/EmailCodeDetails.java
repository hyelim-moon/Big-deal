package com.capstone.dto;

import com.capstone.entity.EmailCode;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

@AllArgsConstructor
public class EmailCodeDetails implements UserDetails {
    private String email;
    private String code;
    private Duration time;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return EmailCodeDetails.authorities();
    }
    public static Collection<? extends  GrantedAuthority> authorities() {
        return Stream.of("ROLE_NEW").map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return this.code;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isCredentialsNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.time.getSeconds() > Duration.ofMillis(new Date().getTime()).getSeconds();
    }

    @Override
    public boolean isEnabled() {
        return isCredentialsNonExpired();
    }
}
