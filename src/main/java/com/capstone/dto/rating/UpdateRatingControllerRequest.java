package com.capstone.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateRatingControllerRequest {
    private String uuid;
    private Integer starRating;
    private String review;
    public UpdateRatingRequest toUpdateRatingRequest() {
        return UpdateRatingRequest.builder()
                .uuid(uuid)
                .starRating(starRating)
                .review(review)
                .build();
    }
}
