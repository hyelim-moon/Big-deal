package com.capstone.service;

import com.capstone.entity.LocalCurrencyFranchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

public interface LocalCurrencyFranchiseService {
    LocalCurrencyFranchise find(Long registerNumber);
    Page<LocalCurrencyFranchise> findBySectorCodeIn(List<Integer> sectorCode, Pageable pageRequest);
    List<LocalCurrencyFranchise> findByLatitudeAndLongitude(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude);
    Page<LocalCurrencyFranchise> findByCityName(String cityName, Pageable pageRequest);
}
