package com.capstone.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {
    private final UserDetailsService userDetailsService;
    private final Key key;
    private final long tokenValidMillisecond;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-time}") long tokenValidMillisecond,
            @Autowired UserDetailsService userDetailsService
            ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidMillisecond = tokenValidMillisecond;
        this.userDetailsService = userDetailsService;
    }
    public String createToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        LocalDateTime localDateTime = LocalDateTime.now();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(localDateTime)
                .setExpiration(LocalDateTime.now().plusNanos(tokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        return token;
    }
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    public String getUsername(String token) {
        String info = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        return info;
    }
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
}
