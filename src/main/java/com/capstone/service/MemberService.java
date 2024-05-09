package com.capstone.service;

import com.capstone.dto.AddMemberRequest;
import com.capstone.dto.MemberInfoResponse;
import com.capstone.dto.MemberResponse;
import com.capstone.dto.UpdateMemberRequest;

import java.util.List;

public interface MemberService {
    MemberInfoResponse findById(String uuid);
    List<MemberResponse> findAll();
    MemberInfoResponse save(AddMemberRequest request);
    MemberInfoResponse update(String uuid, UpdateMemberRequest request);
    void delete(String uuid);

}
