package com.capstone.controller;

import com.capstone.dto.rating.*;
import com.capstone.provider.JwtTokenUtility;
import com.capstone.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
@RestController
public class RatingRestController {
    private final JwtTokenUtility jwtTokenUtility;
    private final RatingService ratingService;

    @GetMapping("{uuid}")
    public ResponseEntity<RatingResponse> findById(@PathVariable String uuid) {
        return ResponseEntity.ok().body(ratingService.findById(uuid));
    }
    @GetMapping("member/{memberUuid}")
    public ResponseEntity<List<RatingResponse>> findByMemberUuid(@PathVariable String memberUuid) {
        return ResponseEntity.ok().body(ratingService.findByMemberUuid(memberUuid));
    }
    @GetMapping("franchise/{franchiseUuid}")
    public ResponseEntity<List<RatingResponse>> findByFranchiseUuid(@PathVariable String franchiseUuid) {
        return ResponseEntity.ok().body(ratingService.findByFranchiseUuid(franchiseUuid));
    }
    @GetMapping("")
    public ResponseEntity<List<RatingResponse>> findAll() {
        return ResponseEntity.ok().body(ratingService.findAll());
    }
    @PostMapping("")
    public ResponseEntity<RatingResponse> insert(@RequestHeader(name = "Authorization", required = true, defaultValue = "") String authorization, @RequestBody AddRatingControllerRequest controllerRequest) {
        AddRatingRequest request = controllerRequest.toAddRatingRequest(jwtTokenUtility.getUsername(jwtTokenUtility.getTokenAtHeader(authorization)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.insert(request));
    }
    @PutMapping("")
    public ResponseEntity<RatingResponse> update(@RequestHeader(name = "Authorization", required = true, defaultValue = "") String authorization, @RequestBody UpdateRatingControllerRequest controllerRequest) {
        UpdateRatingRequest request = controllerRequest.toUpdateRatingRequest();
        return ResponseEntity.ok().body(ratingService.update(jwtTokenUtility.getUsername(jwtTokenUtility.getTokenAtHeader(authorization)), request));
    }
    @DeleteMapping("")
    public ResponseEntity<RatingResponse> remove(@RequestHeader(name = "Authorization", required = true, defaultValue = "") String authorization, @RequestBody RemoveRatingRequest request) {
        return ResponseEntity.ok().body(ratingService.remove(jwtTokenUtility.getUsername(jwtTokenUtility.getTokenAtHeader(authorization)), request));
    }
}
