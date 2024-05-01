package com.capstone.dto;

import com.capstone.entity.KakaoMember;
import com.capstone.entity.Member;
import lombok.Getter;

@Getter
public class KakaoMemberInfoResponse {
    private final Long id;
    private final Member member;
    public KakaoMemberInfoResponse(KakaoMember kakaoMember) {
        this.id = kakaoMember.getId();
        this.member = kakaoMember.getMember();
    }
}
