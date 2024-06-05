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
public class AddRatingControllerRequest {
    @NotEmpty
    private String franchiseUuid;
    @NotNull
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
