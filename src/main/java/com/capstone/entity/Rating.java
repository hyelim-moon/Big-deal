package com.capstone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Temporal(TemporalType.TIMESTAMP)
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
}
