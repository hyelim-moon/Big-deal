package com.capstone.service;

import com.capstone.entity.LocalCurrencyFranchise;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public interface LocalCurrencyFranchiseSearchService {
    LocalCurrencyFranchise find(Long registerNumber);
    List<LocalCurrencyFranchise> find(Integer sectorCode, Pageable pageable);
    List<LocalCurrencyFranchise> find(BigDecimal latitude, BigDecimal longitude, Pageable pageable);
    List<LocalCurrencyFranchise> find(String cityName, Iterator<String> priorities, Pageable pageable);
}
