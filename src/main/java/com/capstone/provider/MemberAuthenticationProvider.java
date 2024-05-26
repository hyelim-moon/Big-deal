package com.capstone.provider;

import com.capstone.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenUtility jwtTokenUtility;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (jwtTokenUtility.validateToken((String)authentication.getCredentials())) {
            authentication.setAuthenticated(true);
        } else {
            authentication = null;
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MemberAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
