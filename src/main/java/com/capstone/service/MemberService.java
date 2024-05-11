package com.capstone.service;

import com.capstone.dto.member.AddMemberRequest;
import com.capstone.dto.member.MemberInfoResponse;
import com.capstone.dto.member.MemberResponse;
import com.capstone.dto.member.UpdateMemberRequest;

import java.util.List;

public interface MemberService {
    MemberInfoResponse findById(String uuid);
    List<MemberResponse> findAll();
    MemberInfoResponse save(AddMemberRequest request);
    MemberInfoResponse update(String uuid, UpdateMemberRequest request);
    void delete(String uuid);

}
