package com.capstone.config;

import com.capstone.filter.JwtAuthenticationFilter;
import com.capstone.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Qualifier("memberServiceImpl")
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorizeRequest) -> {
                    authorizeRequest
                            .requestMatchers(HttpMethod.GET,"/api/v1/member").authenticated()
                            .requestMatchers(HttpMethod.PUT,"/api/v1/member").authenticated()
                            .requestMatchers(HttpMethod.DELETE,"/api/v1/member").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v1/member").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/member/auth/email").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/member/auth/code").permitAll()
                            .requestMatchers("/api/v1/franchise/**").permitAll()
                            .anyRequest().permitAll()
                            ;
                })
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }
}
