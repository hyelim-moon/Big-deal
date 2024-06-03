package com.capstone.controller;

import com.capstone.dto.rating.*;
import com.capstone.provider.JwtTokenUtility;
import com.capstone.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rating")
public class RatingRestController {
    private final JwtTokenUtility jwtTokenUtility;
    private final RatingService ratingService;

    @GetMapping("{uuid}")
    public ResponseEntity<RatingResponse> findByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok().body(ratingService.findById(uuid));
    }
    @GetMapping("/member/{memberUuid}")
    public ResponseEntity<List<RatingResponse>> findByMemberUuid(@PathVariable String memberUuid) {
        return ResponseEntity.ok().body(ratingService.findByMemberUuid(memberUuid));
    }
    @GetMapping("/franchise/{franchiseUuid")
    public ResponseEntity<List<RatingResponse>> findByFranchiseUuid(@PathVariable String franchiseUuid) {
        return ResponseEntity.ok().body(ratingService.findByFranchiseUuid(franchiseUuid));
    }
    @GetMapping("")
    public ResponseEntity<List<RatingResponse>> findAll() {
        return ResponseEntity.ok().body(ratingService.findAll());
    }
    @PostMapping("")
    public ResponseEntity<RatingResponse> insert(@RequestHeader(name = "Authentication", required = true, defaultValue = "") String authentication, @RequestBody AddRatingControllerRequest controllerRequest) {
        AddRatingRequest request = controllerRequest.toAddRatingRequest(jwtTokenUtility.getUsername(jwtTokenUtility.getTokenAtHeader(authentication)));
        return ResponseEntity.ok().body(ratingService.save(request));
    }
    @PutMapping("")
    public ResponseEntity<RatingResponse> update(@RequestHeader(name = "Authentication", required = true, defaultValue = "") String authentication, @RequestBody UpdateRatingControllerRequest controllerRequest) {
        UpdateRatingRequest request = controllerRequest.toUpdateRatingRequest();
        return ResponseEntity.ok().body(ratingService.update(jwtTokenUtility.getUsername(jwtTokenUtility.getTokenAtHeader(authentication)), request));
    }
    @DeleteMapping("")
    public ResponseEntity<RatingResponse> remove(@RequestHeader(name = "Authentication", required = true, defaultValue = "") String authentication, @RequestBody RemoveRatingRequest request) {
        return ResponseEntity.ok().body(ratingService.remove(jwtTokenUtility.getUsername(jwtTokenUtility.getTokenAtHeader(authentication)), request));
    }
}
