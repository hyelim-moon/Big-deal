package com.capstone.entity;

import com.capstone.exception.MemberBadRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column
    private String uuid;
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @CreatedDate
    private LocalDateTime singUpDateTime;
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> authorizations = new ArrayList<>();
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime withdrawalDateTime;
    @Builder
    public Member(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public Member update(String username, String password, String email) {
        this.username = username != null ? username : this.username;
        this.password = password != null ? password : this.password;
        this.email = email != null ? email : this.email;
        return this;
    }
    public Member withdrawal() {
        if (this.withdrawalDateTime != null) {
            throw new MemberBadRequestException();
        }
        this.withdrawalDateTime = LocalDateTime.now();
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorizations.stream().map(SimpleGrantedAuthority::new).toList();
    }
    @Override
    public String getUsername() {
        return this.username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
