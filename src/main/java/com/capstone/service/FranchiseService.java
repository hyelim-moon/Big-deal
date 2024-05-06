package com.capstone.service;

import com.capstone.dto.FranchiseResponse;
import com.capstone.entity.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface FranchiseService {
    Franchise findById(String uuid);
    Page<Franchise> findBySectorCodeIn(List<Integer> sectorCode, Pageable pageRequest);
    List<Franchise> findByLatitudeAndLongitude(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude);
    List<FranchiseResponse> findByCenter(BigDecimal latitude, BigDecimal longitude);
    Page<Franchise> findByCityName(String cityName, Pageable pageRequest);
    // findByPaperAndMobileAndCard
}
