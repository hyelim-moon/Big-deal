package com.capstone.service;

import com.capstone.dto.rating.AddRatingRequest;
import com.capstone.dto.rating.RatingResponse;

import java.util.List;

public interface RatingService {
    RatingResponse findById(String uuid);
    RatingResponse findByMemberUuid(String MemberUuid);
    RatingResponse findByFranchiseUuid(String FranchiseUuid);
    List<RatingResponse> findAll();
    AddRatingRequest save(AddRatingRequest request);
}
