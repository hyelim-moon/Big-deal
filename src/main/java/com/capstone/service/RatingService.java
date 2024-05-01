package com.capstone.service;

import com.capstone.dto.RatingInfoResponse;

public interface RatingService {
    RatingInfoResponse findById(String uuid);
}
