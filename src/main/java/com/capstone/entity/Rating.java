package com.capstone.entity;

import com.capstone.dto.rating.UpdateRatingRequest;
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
    private LocalDateTime deleteDateTime;
    @Builder
    public Rating(String memberUuid, String franchiseUuid, Integer starRating, String review, LocalDateTime registerDateTime, LocalDateTime deleteDateTime) {
        this.memberUuid = memberUuid;
        this.franchiseUuid = franchiseUuid;
        this.starRating = starRating;
        this.review = review;
        this.registerDateTime = registerDateTime;
        this.deleteDateTime = deleteDateTime;
    }
    public Rating update(UpdateRatingRequest request) {
        starRating = request.getStarRating() == null ? starRating : request.getStarRating();
        review = request.getReview() == null ? review : request.getReview();
        return this;
    }
    public Rating remove() {
        if (this.deleteDateTime != null) {
            throw new RatingInvalidateRemoveException("rating duplicate remove");
        }
        this.deleteDateTime = LocalDateTime.now();
        return this;
    }
}
