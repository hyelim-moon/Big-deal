package com.capstone.service;

import com.capstone.dto.rating.AddRatingRequest;
import com.capstone.dto.rating.RatingResponse;
import com.capstone.dto.rating.RemoveRatingRequest;
import com.capstone.dto.rating.UpdateRatingRequest;

import java.util.List;

public interface RatingService {
    RatingResponse findById(String uuid);
    List<RatingResponse> findByMemberUuid(String MemberUuid);
    List<RatingResponse> findByFranchiseUuid(String FranchiseUuid);
    List<RatingResponse> findAll();
    RatingResponse insert(AddRatingRequest request);
    RatingResponse update(String memberUuid, UpdateRatingRequest request);
    RatingResponse remove(String memberUuid, RemoveRatingRequest request);
}
