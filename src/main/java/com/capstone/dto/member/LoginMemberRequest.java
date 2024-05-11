package com.capstone.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginMemberRequest {
    private String id;
    private String password;
}
