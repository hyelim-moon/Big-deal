package com.capstone.controller;

import com.capstone.dto.JwtTokenResponse;
//import com.capstone.dto.franchise.FranchiseRequest;
import com.capstone.dto.franchise.NewFranchiseRequest;
import com.capstone.dto.franchise.NewFranchiseResponse;
import com.capstone.dto.member.*;
//import com.capstone.entity.NewFranchise;
import com.capstone.entity.NewFranchise;
import com.capstone.provider.JwtTokenUtility;
import com.capstone.service.MemberService;
//import com.capstone.service.franchise.FranchiseSave;
import com.capstone.service.franchise.NewFranchiseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@RestController
public class MemberRestController {
    private final MemberService service;
    private final JwtTokenUtility jwtTokenUtility;

    private final NewFranchiseService newFranchiseService;  // NewFranchiseService 추가


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
    @GetMapping("duplication/username/{username}")
    public ResponseEntity<Boolean> checkUsernameDuplication(@PathVariable String username) {
        return ResponseEntity.ok().body(service.existUsername(username));
    }
    @GetMapping("duplication/email/{email}")
    public ResponseEntity<Boolean> checkEmailDuplication(@PathVariable String email) {
        return ResponseEntity.ok().body(service.existEmail(email));
    }
    @PostMapping("")
    public ResponseEntity<MemberResponse> signUp(@RequestBody AddMemberControllerRequest request, @RequestHeader(value = "Authorization", required = true, defaultValue = "") String authorization) {
        String token = jwtTokenUtility.getTokenAtHeader(authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insert(new AddMemberRequest(request.getUsername(), request.getPassword(), jwtTokenUtility.getUsername(token))));
    }
    @PostMapping("auth/email")
    public ResponseEntity<Object> sendEmail(@RequestBody SendToEmailMemberRequest request) {
        service.sendCodeToEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }
    @PostMapping("auth/code")
    public ResponseEntity<JwtTokenResponse> verifiedEmail(@RequestBody VerifiedMemberRequest request) {
        return ResponseEntity.ok().body(service.verifiedEmail(request));
    }
    @PutMapping("")
    public ResponseEntity<MemberResponse> update(@RequestBody UpdateMemberRequest request, @RequestHeader(value = "Authorization", required = true, defaultValue = "") String authorization) {
        String token = jwtTokenUtility.getTokenAtHeader(authorization);
        return ResponseEntity.ok().body(service.update(jwtTokenUtility.getUsername(token), request));
    }
    @DeleteMapping("")
    public ResponseEntity<Boolean> withdrawal(@RequestHeader(value = "Authorization", required = true, defaultValue = "") String authorization) {
        String token = jwtTokenUtility.getTokenAtHeader(authorization);
        return ResponseEntity.ok().body(service.withdrawal(jwtTokenUtility.getUsername(token)));
    }


    // New Franchise 생성 API 추가
    @PostMapping("franchise")
    public ResponseEntity<NewFranchiseResponse> createNewFranchise(@RequestBody NewFranchiseRequest newFranchiseRequest, @RequestHeader(value = "Authorization", required = true, defaultValue = "") String authorization) {
        String token = jwtTokenUtility.getTokenAtHeader(authorization);
        try {
            NewFranchise newFranchise = newFranchiseService.saveNewFranchise(newFranchiseRequest);
            NewFranchiseResponse response = new NewFranchiseResponse();
            response.setId(newFranchise.getId());
            response.setName(newFranchise.getName());
            response.setAddress(newFranchise.getAddress());
            response.setSector(newFranchise.getSector());
            response.setLatitude(newFranchise.getLatitude());
            response.setLongitude(newFranchise.getLongitude());
            response.setCurrencies(newFranchise.getCurrencies());
            response.setCreatedAt(newFranchise.getCreatedAt().toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new NewFranchiseResponse());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new NewFranchiseResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new NewFranchiseResponse());
        }
    }
}
