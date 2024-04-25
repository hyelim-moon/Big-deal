package com.capstone.service;

import com.capstone.entity.LocalCurrencyFranchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface LocalCurrencyFranchiseService {
    LocalCurrencyFranchise find(Long registerNumber);
    Page<LocalCurrencyFranchise> find(List<Integer> sectorCode, Pageable pageable);
    List<LocalCurrencyFranchise> find(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude);
    Page<LocalCurrencyFranchise> find(String cityName, Pageable pageable);
}
