package com.capstone.dto.member;

import com.capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
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
