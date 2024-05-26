package com.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final EmailService emailService;
    private final EmailCodeService emailCodeService;
}
