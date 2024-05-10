package com.capstone.dto.rating;

import com.capstone.entity.Rating;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RatingResponse {
    private final String memberUuid;
    private final String franchiseUuid;
    private final Integer starRating;
    private final String review;
    private final LocalDateTime registerDateTime;
    private final LocalDateTime deleteDateTime;
    public RatingResponse(Rating rating) {
        this.memberUuid = rating.getMemberUuid();
        this.franchiseUuid = rating.getFranchiseUuid();
        this.starRating = rating.getStarRating();
        this.review = rating.getReview();
        this.registerDateTime = rating.getRegisterDateTime();
        this.deleteDateTime = rating.getDeleteDateTime();
    }
}
