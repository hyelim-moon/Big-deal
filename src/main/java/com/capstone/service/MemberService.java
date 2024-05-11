package com.capstone.service;

import com.capstone.dto.member.*;

import java.util.List;

public interface MemberService {
    MemberInfoResponse findById(String uuid);
    List<MemberResponse> findAll();
    MemberInfoResponse save(AddMemberRequest request);
    MemberInfoResponse update(String uuid, UpdateMemberRequest request);
    void delete(String uuid);

    String login(LoginMemberRequest request);

}
