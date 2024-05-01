package com.capstone.dto;

import com.capstone.entity.Member;
import lombok.Getter;

@Getter
public class AddMemberRequest {
    private String id;
    private String password;
    private String email;
    public Member toEntity() {
        return Member.builder()
                .id(id)
                .password(password)
                .email(email)
                .build();
    }
}
