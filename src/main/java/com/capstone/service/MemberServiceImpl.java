package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import com.capstone.entity.Member;
import com.capstone.exception.MemberNotFoundException;
import com.capstone.exception.MemberUsernameDuplicateException;
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
    public MemberResponse findById(String uuid) throws MemberNotFoundException {
        return new MemberResponse(repository.findById(uuid).orElseThrow(MemberNotFoundException::new));
    }

    @Override
    public List<MemberResponse> findAll() {
        return repository.findAll().stream().map(MemberResponse::new).toList();
    }

    @Override
    public MemberResponse save(AddMemberRequest request) throws NullPointerException, MemberUsernameDuplicateException {
        Member member = repository.findByUsername(request.getUsername());
        if (member != null) {
            throw new MemberUsernameDuplicateException();
        }
        if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
            throw new BadCredentialsException("require data missing.");
        }
        return new MemberResponse(repository.save(request.toEntity()));
    }

    @Override
    public MemberResponse update(String uuid, UpdateMemberRequest request) {
        return new MemberResponse(repository.findById(uuid).orElseThrow(IllegalArgumentException::new).update(request.getId(), request.getPassword(), request.getEmail()));
    }

    @Override
    public void delete(String uuid) {
        repository.deleteById(uuid);
    }

    @Override
    public JwtTokenResponse login(LoginMemberRequest request) throws MemberNotFoundException, BadCredentialsException{
        Member member = repository.findByUsername(request.getUsername());
        if (member == null) {
            throw new MemberNotFoundException();
        }
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
