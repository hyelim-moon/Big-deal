package com.capstone.dto.rating;

import com.capstone.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UpdateRatingRequest {
    private String uuid;
    private Integer starRating;
    private String review;
}
