package com.capstone.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRequest {
    private String username;
    private String password;
    private String email;
}
