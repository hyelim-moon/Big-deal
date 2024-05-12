package com.capstone.service;

import com.capstone.dto.member.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface MemberService extends UserDetailsService {
    MemberInfoResponse findById(String uuid);
    List<MemberResponse> findAll();
    MemberInfoResponse save(AddMemberRequest request);
    MemberInfoResponse update(String uuid, UpdateMemberRequest request);
    void delete(String uuid);

    String login(LoginMemberRequest request);
    UserDetails loadUserByUsername(String username);

}
