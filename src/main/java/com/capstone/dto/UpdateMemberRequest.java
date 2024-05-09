package com.capstone.dto;

import com.capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRequest {
    private String id;
    private String password;
    private String email;
}
