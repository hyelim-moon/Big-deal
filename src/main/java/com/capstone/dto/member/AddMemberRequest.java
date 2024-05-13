package com.capstone.dto.member;

import com.capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddMemberRequest {
    private String username;
    private String password;
    private String email;
    public Member toEntity() {
        return Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
    }
}
