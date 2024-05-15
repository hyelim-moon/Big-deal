package com.capstone.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse {
    private String grantType;
    private String accessToken;
    // private String refreshToken;
}
