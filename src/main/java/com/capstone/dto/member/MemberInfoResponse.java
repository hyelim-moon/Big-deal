package com.capstone.dto.member;

import com.capstone.entity.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberInfoResponse {
    private final String id;
    private final String email;
    private final LocalDateTime singUpDateTime;
    private final LocalDateTime withdrawalDateTime;
    public MemberInfoResponse(Member member) {
        this.id = member.getUsername();
        this.email = member.getEmail();
        this.singUpDateTime = member.getSingUpDateTime();
        this.withdrawalDateTime = member.getWithdrawalDateTime();
    }
}
