package com.capstone.service;

import com.capstone.dto.rating.AddRatingRequest;
import com.capstone.dto.rating.RatingResponse;
import com.capstone.dto.rating.RemoveRatingRequest;
import com.capstone.dto.rating.UpdateRatingRequest;
import com.capstone.entity.Rating;
import com.capstone.exception.RatingDuplicateException;
import com.capstone.exception.RatingInvalidateRemoveException;
import com.capstone.exception.RatingInvalidateUpdateException;
import com.capstone.exception.RatingNotFoundException;
import com.capstone.repository.RatingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    @Override
    public RatingResponse findById(String uuid) {
        return new RatingResponse(ratingRepository.findById(uuid).orElseThrow(RatingNotFoundException::new));
    }

    @Override
    public List<RatingResponse> findByMemberUuid(String memberUuid) {
        return ratingRepository.findByMemberUuid(memberUuid).stream().map(RatingResponse::new).toList();
    }

    @Override
    public List<RatingResponse> findByFranchiseUuid(String franchiseUuid) {
        return ratingRepository.findByMemberUuid(franchiseUuid).stream().map(RatingResponse::new).toList();
    }

    @Override
    public List<RatingResponse> findAll() {
        return ratingRepository.findAll().stream().map(RatingResponse::new).toList();
    }

    @Override
    public RatingResponse save(AddRatingRequest request) {
        if (ratingRepository.findByMemberUuidAndFranchiseUuid(request.getMemberUuid(), request.getFranchiseUuid()) != null) {
            throw new RatingDuplicateException("rating already exists");
        }
        return new RatingResponse(ratingRepository.save(request.toEntity()));
    }

    @Transactional
    @Override
    public RatingResponse update(String memberUuid, UpdateRatingRequest request) {
        Rating rating = ratingRepository.findById(request.getUuid()).orElseThrow(RatingNotFoundException::new);
        if (!rating.getMemberUuid().equals(memberUuid)) {
            throw new RatingInvalidateUpdateException("does not match member uuid");
        }
        return new RatingResponse(rating.update(request));
    }

    @Transactional
    @Override
    public RatingResponse remove(String memberUuid, RemoveRatingRequest request) {
        Rating rating = ratingRepository.findById(request.getUuid()).orElseThrow(RatingNotFoundException::new);
        if (!rating.getMemberUuid().equals(memberUuid)) {
            throw new RatingInvalidateRemoveException("does not match member uuid");
        }
        return new RatingResponse(rating.remove());
    }
}
