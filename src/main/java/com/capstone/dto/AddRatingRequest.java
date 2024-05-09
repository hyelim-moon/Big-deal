package com.capstone.dto;

import com.capstone.entity.Rating;

public class AddRatingRequest {
    private String memberUuid;
    private String franchiseUuid;
    private Integer starRating;
    private String review;
    public Rating toEntity() {
        return Rating.builder()
                .memberUuid(memberUuid)
                .franchiseUuid(franchiseUuid)
                .starRating(starRating)
                .review(review)
                .build();
    }
}
