package com.capstone.controller;

import com.capstone.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorizationRestController {
    private final AuthorizationService authorizationService;
}
