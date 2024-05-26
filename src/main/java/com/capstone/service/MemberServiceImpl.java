package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.SetEmailCode;
import com.capstone.dto.member.*;
import com.capstone.entity.EmailCode;
import com.capstone.entity.Member;
import com.capstone.exception.*;
import com.capstone.provider.JwtTokenUtility;
import com.capstone.provider.PinNumberUtility;
import com.capstone.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service("memberServiceImpl")
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository repository;
    private final JwtTokenUtility jwtTokenUtility;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailCodeService emailCodeService;
    private final PinNumberUtility pinNumberUtility;
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

    @Transactional
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
        if (repository.existsByEmail(email)) {
            throw new MemberEmailDuplicateException();
        }
        String title = "이메일 인증 번호";
        String authCode = pinNumberUtility.createCode();
        emailService.sendEmail(email, title, authCode);
        emailCodeService.setValues(SetEmailCode.builder()
                .email(email)
                .code(authCode)
                .time(Duration.ofMillis(this.authCodeExpirationMillis))
                .build());
    }
    @Override
    public JwtTokenResponse verifiedEmail(VerifiedMemberRequest request) {
        if (emailCodeService.getValues(request.getEmail()) == null) {
            throw new EmailNotFoundException();
        }
        if (!emailCodeService.getValues(request.getEmail()).equals(request.getCode())) {
            throw new EmailInvalidateCodeException();
        }
        return new JwtTokenResponse("Bearer", jwtTokenUtility.createToken(request.getEmail(), EmailCode.authorities().stream().map(GrantedAuthority::getAuthority).toList()));
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
        String accessToken = jwtTokenUtility.createToken(member.getUuid(), List.of("ROLE_USER"));
        return new JwtTokenResponse("Bearer", accessToken);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findById(username).orElseThrow(()->new UsernameNotFoundException("member not found."));
    }
}
