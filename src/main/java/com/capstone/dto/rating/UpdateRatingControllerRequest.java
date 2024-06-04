package com.capstone.dto.rating;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateRatingControllerRequest {
    @NotEmpty
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
