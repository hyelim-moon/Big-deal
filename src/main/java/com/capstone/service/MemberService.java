package com.capstone.service;

import com.capstone.dto.AddMemberRequest;
import com.capstone.dto.MemberInfoResponse;

import java.util.List;

public interface MemberService {
    MemberInfoResponse findById(String uuid);
    List<MemberInfoResponse> findAll();
    AddMemberRequest save(AddMemberRequest request);

}
