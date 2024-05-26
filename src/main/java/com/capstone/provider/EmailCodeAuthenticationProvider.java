package com.capstone.provider;

import com.capstone.entity.EmailCode;
import com.capstone.service.EmailCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailCodeAuthenticationProvider implements AuthenticationProvider {
    private final EmailCodeService emailCodeService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String code = emailCodeService.getValues(authentication.getName());
        if (authentication.getCredentials().equals(code)) {
            authentication = new EmailCodeAuthenticationToken((String)authentication.getPrincipal(), (String)authentication.getCredentials(), EmailCode.authorities());
            authentication.setAuthenticated(true);
        } else {
            authentication = null;
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
