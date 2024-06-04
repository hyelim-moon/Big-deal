package com.capstone.service;

import com.capstone.dto.rating.AddRatingRequest;
import com.capstone.dto.rating.RatingResponse;
import com.capstone.dto.rating.RemoveRatingRequest;
import com.capstone.dto.rating.UpdateRatingRequest;
import com.capstone.entity.Rating;
import com.capstone.exception.*;
import com.capstone.repository.FranchiseRepository;
import com.capstone.repository.RatingRepository;
import com.capstone.service.franchise.FranchiseNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final MemberService memberService;
    private final FranchiseService franchiseService;
    @Override
    public RatingResponse findById(String uuid) {
        Rating rating = ratingRepository.findById(uuid).orElseThrow(RatingNotFoundException::new);
        if (rating.getRemoveDateTime() != null) {
            throw new RatingNotFoundException("member is removed.");
        }
        return new RatingResponse(rating);
    }

    @Override
    public List<RatingResponse> findByMemberUuid(String memberUuid) {
        if (!memberService.exist(memberUuid)) {
            throw new MemberNotFoundException("member does not exists");
        }
        return ratingRepository.findByMemberUuid(memberUuid).stream().filter((rating) -> rating.getRemoveDateTime() == null).map(RatingResponse::new).toList();
    }

    @Override
    public List<RatingResponse> findByFranchiseUuid(String franchiseUuid) {
        if (!franchiseService.exist(franchiseUuid)) {
            throw new FranchiseNotFoundException("franchise does not exists");
        }
        return ratingRepository.findByMemberUuid(franchiseUuid).stream().filter((rating) -> rating.getRemoveDateTime() == null).map(RatingResponse::new).toList();
    }

    @Override
    public List<RatingResponse> findAll() {
        return ratingRepository.findAll().stream().map(RatingResponse::new).toList();
    }

    @Override
    public RatingResponse insert(AddRatingRequest request) {
        if (ratingRepository.findByMemberUuidAndFranchiseUuid(request.getMemberUuid(), request.getFranchiseUuid()) != null) {
            throw new RatingDuplicateException("rating already exists");
        }
        if (!memberService.exist(request.getMemberUuid())) {
            throw new MemberNotFoundException("member token weird.");
        }
        if (!franchiseService.exist(request.getFranchiseUuid())) {
            throw new FranchiseNotFoundException("franchise does not exists");
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
