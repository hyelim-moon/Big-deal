package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import com.capstone.entity.Member;
import com.capstone.jwt.JwtTokenProvider;
import com.capstone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("memberServiceImpl")
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository repository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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
    public JwtTokenResponse login(LoginMemberRequest request) {
        Member member = repository.findByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("login fail.");
        }
        String accessToken = jwtTokenProvider.createToken(request.getUsername(), List.of("user"));
        return new JwtTokenResponse("Bearer", accessToken);
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username);
    }
}
