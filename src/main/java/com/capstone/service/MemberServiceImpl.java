package com.capstone.service;

import com.capstone.dto.member.*;
import com.capstone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("memberServiceImpl")
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository repository;

    @Override
    public MemberInfoResponse findById(String uuid) {
        return new MemberInfoResponse(repository.findById(uuid).orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public List<MemberResponse> findAll() {
        return repository.findAll().stream().map(MemberResponse::new).toList();
    }

    @Override
    public MemberInfoResponse save(AddMemberRequest request) {
        return new MemberInfoResponse(repository.save(request.toEntity()));
    }

    @Override
    public MemberInfoResponse update(String uuid, UpdateMemberRequest request) {
        return new MemberInfoResponse(repository.findById(uuid).orElseThrow(IllegalArgumentException::new).update(request.getId(), request.getPassword(), request.getEmail()));
    }

    @Override
    public void delete(String uuid) {
        repository.deleteById(uuid);
    }

    @Override
    public String login(LoginMemberRequest request) {
        String accessToken = null;
        return accessToken;
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username);
    }
}
