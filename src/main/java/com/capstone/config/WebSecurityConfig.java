package com.capstone.config;

import com.capstone.filter.EmailCodeAuthenticationFilter;
import com.capstone.filter.JwtAuthenticationFilter;
import com.capstone.provider.EmailCodeAuthenticationProvider;
import com.capstone.provider.JwtTokenUtility;
import com.capstone.provider.MemberAuthenticationProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@RequiredArgsConstructor
@Qualifier("memberServiceImpl")
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private final JwtTokenUtility jwtTokenUtility;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final ObjectMapper om;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtility, authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorizeRequest) -> {
                    authorizeRequest
                            .requestMatchers(HttpMethod.GET,"/api/v1/member").hasAuthority("ROLE_USER")
                            .requestMatchers(HttpMethod.PUT,"/api/v1/member").hasAuthority("ROLE_USER")
                            .requestMatchers(HttpMethod.DELETE,"/api/v1/member").hasAuthority("ROLE_USER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/member").hasAuthority("ROLE_NEW")
                            .requestMatchers(HttpMethod.POST, "/api/v1/member/auth/email").permitAll()
                            .requestMatchers("/api/v1/franchise/**").permitAll()
                            .anyRequest().permitAll()
                            ;
                })
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }
    @Order(1)
    @Bean
    public SecurityFilterChain emailCodeFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .securityMatcher("/api/v1/member/auth/code")
                .addFilterBefore(new EmailCodeAuthenticationFilter(authenticationManager, om), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorizeRequest) -> {
                    authorizeRequest
                            .requestMatchers(HttpMethod.POST, "/api/v1/member/auth/code").authenticated()
                            .anyRequest().permitAll();
                })
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();

    }
    @Bean
    public AuthenticationManager authenticationManager(MemberAuthenticationProvider memberAuthenticationProvider, EmailCodeAuthenticationProvider emailCodeAuthenticationProvider) {
        return new ProviderManager(List.of(memberAuthenticationProvider, emailCodeAuthenticationProvider));
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
