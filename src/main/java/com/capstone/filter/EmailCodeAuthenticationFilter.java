package com.capstone.filter;

import com.capstone.dto.EmailCodeDetails;
import com.capstone.dto.member.AddMemberControllerRequest;
import com.capstone.dto.member.VerifiedMemberRequest;
import com.capstone.provider.CachedBodyServletHttpRequest;
import com.capstone.provider.CachedBodyServletInputStream;
import com.capstone.provider.EmailCodeAuthenticationToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

@AllArgsConstructor
@Component
public class EmailCodeAuthenticationFilter extends OncePerRequestFilter {
    private AuthenticationManager authenticationManager;
    private ObjectMapper om;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CachedBodyServletHttpRequest cachedBodyServletHttpRequest = new CachedBodyServletHttpRequest(request);
        BufferedReader reader = cachedBodyServletHttpRequest.getReader();
        String body = "", line;
        while ((line = reader.readLine()) != null) {
            body = body.concat(line);
        }
        System.out.println(body);
        JsonNode root = om.readTree(body);
        VerifiedMemberRequest readValue = new VerifiedMemberRequest(root.get("email").asText(), root.get("code").asText());

        EmailCodeAuthenticationToken token = new EmailCodeAuthenticationToken(readValue.getEmail(), readValue.getCode(), null);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(cachedBodyServletHttpRequest, response);
    }
}
