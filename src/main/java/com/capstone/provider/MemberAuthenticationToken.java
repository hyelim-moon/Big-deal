package com.capstone.provider;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MemberAuthenticationToken extends AbstractAuthenticationToken {
    private String uuid;
    private String token;
    public MemberAuthenticationToken(String uuid, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uuid = uuid;
        this.token = token;
    }
    public MemberAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }
    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return uuid;
    }
}
