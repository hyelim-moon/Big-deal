package com.capstone.filter;

import com.capstone.exception.MemberNotFoundException;
import com.capstone.jwt.JwtTokenProvider;
import com.capstone.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = jwtTokenProvider.resolveToken(request);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            String token = jwtTokenProvider.getTokenAtHeader(authorizationHeader);
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (MemberNotFoundException e) {
                    response.setStatus(403);
                    filterChain.doFilter(request, response);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
