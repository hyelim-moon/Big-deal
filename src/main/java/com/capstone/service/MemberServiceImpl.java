package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import com.capstone.entity.Member;
import com.capstone.exception.*;
import com.capstone.jwt.JwtTokenProvider;
import com.capstone.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service("memberServiceImpl")
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private final MemberRepository repository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisService redisService;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Override
    public MemberResponse findById(String uuid) throws MemberNotFoundException {
        return new MemberResponse(repository.findById(uuid).orElseThrow(MemberNotFoundException::new));
    }

    @Override
    public List<MemberResponse> findAll() {
        return repository.findAll().stream().map(MemberResponse::new).toList();
    }

    @Override
    public MemberResponse insert(AddMemberRequest request) throws MemberUsernameDuplicateException, MemberEmailDuplicateException, MemberBadRequestException {
        try {
            if (request.getUsername().isEmpty() || request.getPassword().isEmpty() || request.getEmail().isEmpty()) {
                throw new MemberBadRequestException();
            }
            if (repository.existsByUsername(request.getUsername())) {
                throw new MemberUsernameDuplicateException();
            }
            if (repository.existsByEmail(request.getEmail())) {
                throw new MemberEmailDuplicateException();
            }
            return new MemberResponse(repository.save(request.toEntity(passwordEncoder)));
        } catch (NullPointerException nullPointerException) {
            if (request == null) {
                System.err.println("request parameter is null.");
            } else {
                if (request.getUsername() == null) {
                    throw new MemberBadRequestException("username member field is null.");
                }
                if (request.getPassword() == null) {
                    throw new MemberBadRequestException("password member field is null.");
                }
                if (request.getEmail() == null) {
                    throw new MemberBadRequestException("email member field is null.");
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
        Member member = repository.findById(uuid).orElseThrow(MemberNotFoundException::new);
        if (repository.existsByUsername(request.getUsername()) && !member.getUsername().equals(request.getUsername())) {
            throw new MemberUsernameDuplicateException();
        }
        if (member.getWithdrawalDateTime() != null) {
            throw new MemberInvalidateUpdateException();
        }
        return new MemberResponse(member.update(request.getUsername(), request.getPassword(), request.getEmail()));
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
    public void sendCodeToEmail(String email) {
        String title = "이메일 인증 번호";
        String authCode = createCode();
        mailService.sendEmail(email, title, authCode);
        redisService.setValues(AUTH_CODE_PREFIX + email, authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }
    @Override
    public Boolean verifiedCode(String email, String authCode) {
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        return redisAuthCode != null && redisAuthCode.equals(authCode);
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
        if (member.getWithdrawalDateTime() != null) {
            throw new MemberInvalidateLoginException();
        }
        String accessToken = jwtTokenProvider.createToken(member.getUuid(), List.of("user"));
        return new JwtTokenResponse("Bearer", accessToken);
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findById(username).orElseThrow(MemberNotFoundException::new);
    }
    private String createCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("no such algorithm", e);
        }
    }
}
