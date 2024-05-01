package com.capstone.service;

import com.capstone.dto.MemberInfoResponse;

public interface MemberService {
    MemberInfoResponse findById(String uuid);
}
