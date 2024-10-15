package com.capstone.filter;

import com.capstone.provider.JwtTokenUtility;
import com.capstone.provider.MemberAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtility jwtTokenUtility;
    private final AuthenticationManager authenticationManager;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = jwtTokenUtility.resolveToken(request);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            String token = jwtTokenUtility.getTokenAtHeader(authorizationHeader);
            String username = jwtTokenUtility.getUsername(token);
            Collection<? extends GrantedAuthority> authorities = jwtTokenUtility.getAuthentication(token);

            MemberAuthenticationToken memberAuthenticationToken = new MemberAuthenticationToken(username, token, authorities);
            Authentication authentication = authenticationManager.authenticate(memberAuthenticationToken);
            SecurityContextHolder.getContext


                    ().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
