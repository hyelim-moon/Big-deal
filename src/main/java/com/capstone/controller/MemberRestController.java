package com.capstone.controller;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.LoginMemberRequest;
import com.capstone.dto.member.MemberInfoResponse;
import com.capstone.dto.member.MemberResponse;
import com.capstone.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@RestController
public class MemberRestController {
    private final MemberService service;
    @GetMapping("")
    public ResponseEntity<List<MemberResponse>> findByAll() {
        return ResponseEntity.ok().body(service.findAll());
    }
    @PostMapping("auth/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginMemberRequest request) {
        return ResponseEntity.ok().body(service.login(request));
    }
    @GetMapping("{uuid}")
    public ResponseEntity<MemberInfoResponse> findById(@PathVariable String uuid) {
        return ResponseEntity.ok().body(service.findById(uuid));
    }
}
