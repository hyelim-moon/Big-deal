package com.capstone.repository;

import com.capstone.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {
    List<Rating> findByMemberUuid(String memberUuid);
    List<Rating> findByFranchiseUuid(String franchiseUuid);

    Rating findByMemberUuidAndFranchiseUuid(String memberUuid, String franchiseUuid);
}
