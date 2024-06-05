package com.capstone.entity;

import com.capstone.dto.rating.UpdateRatingRequest;
import com.capstone.exception.RatingDuplicateRemoveException;
import com.capstone.exception.RatingInvalidateRemoveException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Rating {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column
    private String uuid;
    @Column(nullable = false)
    private String memberUuid;
    @Column(nullable = false)
    private String franchiseUuid;
    @Column
    private Integer starRating;
    @Column
    private String review;
    @CreatedDate
    private LocalDateTime registerDateTime;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime removeDateTime;
    @Builder
    public Rating(String memberUuid, String franchiseUuid, Integer starRating, String review, LocalDateTime registerDateTime, LocalDateTime removeDateTime) {
        this.memberUuid = memberUuid;
        this.franchiseUuid = franchiseUuid;
        this.starRating = starRating;
        this.review = review;
        this.registerDateTime = registerDateTime;
        this.removeDateTime = removeDateTime;
    }
    public Rating update(UpdateRatingRequest request) {
        starRating = request.getStarRating() == null ? starRating : request.getStarRating();
        review = request.getReview() == null ? review : request.getReview();
        return this;
    }
    public Rating remove() {
        if (this.removeDateTime != null) {
            throw new RatingDuplicateRemoveException("rating duplicate remove.");
        }
        this.removeDateTime = LocalDateTime.now();
        return this;
    }
}
