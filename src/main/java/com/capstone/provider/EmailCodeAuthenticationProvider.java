package com.capstone.provider;

import com.capstone.dto.EmailCodeDetails;
import com.capstone.service.EmailCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailCodeAuthenticationProvider implements AuthenticationProvider {
    private final EmailCodeService emailCodeService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = emailCodeService.loadUserByUsername(authentication.getName());
        if (!authentication.getCredentials().equals(userDetails.getPassword())) {
            throw new BadCredentialsException("pin number different.");
        }
        authentication = new EmailCodeAuthenticationToken((String)authentication.getPrincipal(), (String)authentication.getCredentials(), EmailCodeDetails.authorities());
        authentication.setAuthenticated(true);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
