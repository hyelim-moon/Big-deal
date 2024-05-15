package com.capstone.config;

import com.capstone.dto.ErrorCodeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private ObjectMapper om;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        ErrorCodeResponse message = ErrorCodeResponse.of("unauthorized");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            PrintWriter writer = response.getWriter();
            writer.write(om.writeValueAsString(message));
            writer.flush();
        } catch (JsonProcessingException jsonE) {
            System.err.println("[my]:json processing exception occur in CustomAuthenticationEntryPoint");
            throw new RuntimeException("[my]:json processing exception occur in CustomAuthenticationEntryPoint", jsonE);
        } catch (IOException ioe) {
            System.err.println("[my]:io exception occur CustomAuthenticationEntryPoint");
            throw new RuntimeException("[my]:io exception occur CustomAuthenticationEntryPoint", ioe);
        }
    }
}
