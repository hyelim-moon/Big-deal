package com.capstone.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddRatingControllerRequest {
    private String franchiseUuid;
    private Integer starRating;
    private String review;
    public AddRatingRequest toAddRatingRequest(String uuid) {
        return AddRatingRequest.builder()
                .memberUuid(uuid)
                .franchiseUuid(franchiseUuid)
                .starRating(starRating)
                .review(review)
                .build();
    }
}
