package com.capstone.controller;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import com.capstone.provider.JwtTokenProvider;
import com.capstone.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@RestController
public class MemberRestController {
    private final MemberService service;
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("")
    public ResponseEntity<List<MemberResponse>> findByAll() {
        return ResponseEntity.ok().body(service.findAll());
    }
    @PostMapping("auth/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginMemberRequest request) {
        return ResponseEntity.ok().body(service.login(request));
    }
    @GetMapping("{uuid}")
    public ResponseEntity<MemberResponse> findById(@PathVariable String uuid) {
        return ResponseEntity.ok().body(service.findById(uuid));
    }
    @PostMapping("")
    public ResponseEntity<MemberResponse> signUp(@RequestBody AddMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insert(request));
    }
    @PostMapping("auth/email")
    public ResponseEntity<Object> sendEmail(@RequestBody SendToEmailMemberRequest request) {
        service.sendCodeToEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }
    @PutMapping("")
    public ResponseEntity<MemberResponse> update(@RequestBody UpdateMemberRequest request, @RequestHeader(value = "Authorization", required = true, defaultValue = "") String authorization) {
        String token = jwtTokenProvider.getTokenAtHeader(authorization);
        return ResponseEntity.ok().body(service.update(jwtTokenProvider.getUsername(token), request));
    }
    @DeleteMapping("")
    public ResponseEntity<Boolean> withdrawal(@RequestHeader(value = "Authorization", required = true, defaultValue = "") String authorization) {
        String token = jwtTokenProvider.getTokenAtHeader(authorization);
        return ResponseEntity.ok().body(service.withdrawal(jwtTokenProvider.getUsername(token)));
    }
}
