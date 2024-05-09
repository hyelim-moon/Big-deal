package com.capstone.dto;

import com.capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberResponse {
    private String uuid;
    private String id;
    private String email;
    public MemberResponse(Member member) {
        this.uuid = member.getUuid();
        this.id = member.getId();
        this.email = member.getEmail();
    }
}
