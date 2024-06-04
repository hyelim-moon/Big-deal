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
public class UpdateRatingRequest {
    @NotEmpty
    private String uuid;
    private Integer starRating;
    private String review;
}
