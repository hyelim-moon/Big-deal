package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface MemberService extends UserDetailsService {
    MemberResponse findById(String uuid);
    List<MemberResponse> findAll();
    MemberResponse save(AddMemberRequest request);
    MemberResponse update(String uuid, UpdateMemberRequest request);
    void delete(String uuid);

    JwtTokenResponse login(LoginMemberRequest request);
    UserDetails loadUserByUsername(String username);

}
