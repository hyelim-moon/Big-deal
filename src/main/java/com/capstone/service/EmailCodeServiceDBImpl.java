package com.capstone.service;

import com.capstone.dto.SetEmailCode;
import com.capstone.entity.EmailCode;
import com.capstone.repository.EmailCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Profile("dev")
@Primary
@Service("emailCodeServiceDBImpl")
@RequiredArgsConstructor
public class EmailCodeServiceDBImpl implements EmailCodeService {
    private final EmailCodeRepository emailCodeRepository;
    @Override
    public void setValues(SetEmailCode setEmailCode) {
        EmailCode emailCode = emailCodeRepository.findById(setEmailCode.getEmail()).orElse(new EmailCode(setEmailCode.getEmail(), setEmailCode.getCode(), setEmailCode.getTime()));
        emailCode.overwrite(setEmailCode.getCode(), setEmailCode.getTime());
        emailCodeRepository.save(emailCode);
    }
    @Override
    public String getValues(String k) {
        try {
            return emailCodeRepository.findById(k).orElseThrow().getCode();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return emailCodeRepository.findById(username).orElseThrow(()-> new UsernameNotFoundException("member not found")).details();
    }
}
