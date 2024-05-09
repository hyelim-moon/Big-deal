package com.capstone.service;

import com.capstone.dto.AddRatingRequest;
import com.capstone.dto.RatingInfoResponse;

import java.util.List;

public interface RatingService {
    RatingInfoResponse findById(String uuid);
    RatingInfoResponse findByMemberUuid(String MemberUuid);
    RatingInfoResponse findByFranchiseUuid(String FranchiseUuid);
    List<RatingInfoResponse> findAll();
    AddRatingRequest save(AddRatingRequest request);
}
