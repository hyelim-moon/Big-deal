package com.capstone.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class EmailCodeAuthenticationToken extends AbstractAuthenticationToken {
    private String email;
    private String code;
    public EmailCodeAuthenticationToken(String email, String code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.email = email;
        this.code = code;
    }
    public EmailCodeAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return this.code;
    }

    @Override
    public Object getPrincipal() {
        return this.email;
    }
}
