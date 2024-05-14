package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import com.capstone.entity.Member;
import com.capstone.exception.*;
import com.capstone.jwt.JwtTokenProvider;
import com.capstone.repository.MemberRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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
    public MemberResponse insert(AddMemberRequest request) throws MemberUsernameDuplicateException, MemberEmailDuplicateException {
        try {
            if (repository.existsByUsername(request.getUsername())) {
                throw new MemberUsernameDuplicateException();
            }
            if (repository.existsByEmail(request.getEmail())) {
                throw new MemberEmailDuplicateException();
            }
            if (request.getUsername().isEmpty() || request.getPassword().isEmpty() || request.getEmail().isEmpty()) {
                throw new MemberBadRequestException();
            }
            return new MemberResponse(repository.save(request.toEntity(passwordEncoder)));
        } catch (NullPointerException nullPointerException) {
            if (request == null) {
                System.err.println("request parameter is null.");
            } else {
                if (request.getUsername() == null) {
                    System.err.println("username member field is null.");
                }
                if (request.getPassword() == null) {
                    System.err.println("password member field is null.");
                }
                if (request.getEmail() == null) {
                    System.err.println("email member field is null.");
                }
            }
            if (passwordEncoder == null) {
                System.err.println("password encoder bean is null.");
            }
            throw nullPointerException;
        }
    }

    @Override
    public MemberResponse update(String uuid, UpdateMemberRequest request) throws  MemberUsernameDuplicateException, MemberNotFoundException{
        if (repository.existsByUsername(request.getUsername())) {
            throw new MemberUsernameDuplicateException();
        }
        return new MemberResponse(repository.findById(uuid).orElseThrow(MemberNotFoundException::new).update(request.getUsername(), request.getPassword(), request.getEmail()));
    }

    @Override
    public void delete(String uuid) throws MemberNotFoundException{
        try {
            repository.deleteById(uuid);
        } catch (EmptyResultDataAccessException e) {
            throw new MemberNotFoundException(e);
        }
    }
    @Transactional
    @Override
    public Boolean withdrawal(String uuid) {
        repository.findById(uuid).orElseThrow(MemberNotFoundException::new).withdrawal();
        return true;
    }

    @Override
    public JwtTokenResponse login(LoginMemberRequest request) throws MemberBadRequestException, MemberNotFoundException, MemberPasswordNotEqualsException {
        Member member = repository.findByUsername(request.getUsername());
        if (request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
            throw new MemberBadRequestException();
        }
        if (!repository.existsByUsername(request.getUsername())) {
            throw new MemberNotFoundException();
        }
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberPasswordNotEqualsException();
        }
        String accessToken = jwtTokenProvider.createToken(member.getUuid(), List.of("user"));
        return new JwtTokenResponse("Bearer", accessToken);
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findById(username).orElseThrow(MemberNotFoundException::new);
    }
}
