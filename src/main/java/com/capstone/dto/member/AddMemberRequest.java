package com.capstone.dto.member;

import com.capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddMemberRequest {
    private String username;
    private String password;
    private String email;
    private String code;
    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
    }
}
