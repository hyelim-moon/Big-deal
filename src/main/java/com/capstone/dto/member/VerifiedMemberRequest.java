package com.capstone.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VerifiedMemberRequest {
    private String email;
    private String code;
}
