package com.capstone.controller;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import com.capstone.jwt.JwtTokenProvider;
import com.capstone.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    @PutMapping("")
    public ResponseEntity<MemberResponse> update(@RequestBody UpdateMemberRequest request, @RequestHeader(value = "Authorization", required = false, defaultValue = "") String authorization) {
        String token = authorization.substring(7);
        return ResponseEntity.ok().body(service.update(jwtTokenProvider.getUsername(token), request));
    }
    @DeleteMapping("")
    public ResponseEntity<Boolean> withdrawal(@RequestHeader(value = "Authorization", required = false, defaultValue = "") String authorization) {
        String token = authorization.substring(7);
        return ResponseEntity.ok().body(service.withdrawal(jwtTokenProvider.getUsername(token)));
    }
}
