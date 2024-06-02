package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface MemberService extends UserDetailsService {
    MemberResponse findById(String uuid);
    List<MemberResponse> findAll();
    MemberResponse insert(AddMemberRequest request);
    MemberResponse update(String uuid, UpdateMemberRequest request);
    void delete(String uuid);
    Boolean withdrawal(String uuid);
    void sendCodeToEmail(String email);
    JwtTokenResponse verifiedEmail(VerifiedMemberRequest request);

    JwtTokenResponse login(LoginMemberRequest request);

    Boolean existUsername(String username);

    Boolean existEmail(String email);
}
