package com.capstone.dto;

import com.capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class MemberInfoResponse {
    private final String id;
    private final String email;
    private final LocalDateTime singUpDateTime;
    private final LocalDateTime singOutDateTime;
    public MemberInfoResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.singUpDateTime = member.getSingUpDateTime();
        this.singOutDateTime = member.getSingOutDateTime();
    }
}
