package com.capstone.dto.rating;

import com.capstone.entity.Rating;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Validated
@AllArgsConstructor
@Builder
@Getter
public class AddRatingRequest {
    @NotEmpty
    private String memberUuid;
    @NotEmpty
    private String franchiseUuid;
    @NotNull
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
