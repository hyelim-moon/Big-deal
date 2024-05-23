package com.capstone.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class EmailCodeAuthenticationToken extends AbstractAuthenticationToken {
    private UserDetails userDetails;
    private String code;
    public EmailCodeAuthenticationToken(UserDetails userDetails, String code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userDetails = userDetails;
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
        return this.userDetails;
    }
}
