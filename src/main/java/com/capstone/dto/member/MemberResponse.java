package com.capstone.dto.member;

import com.capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberResponse {
    private String uuid;
    private String id;
    private String email;
    private LocalDateTime singUpDateTime;
    private LocalDateTime singOutDateTime;
    public MemberResponse(Member member) {
        this.uuid = member.getUuid();
        this.id = member.getUsername();
        this.email = member.getEmail();
        this.singUpDateTime = member.getSingUpDateTime();
        this.singOutDateTime = member.getSingOutDateTime();
    }
}
