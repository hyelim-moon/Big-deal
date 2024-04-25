package com.capstone.service;

import com.capstone.entity.LocalCurrencyFranchise;
import com.capstone.entity.LocalCurrencyFranchiseSpecification;
import com.capstone.repository.LocalCurrencyFranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("localCurrencyFranchiseServiceImpl")
@RequiredArgsConstructor
public class LocalCurrencyFranchiseServiceImpl implements LocalCurrencyFranchiseService {
    private final LocalCurrencyFranchiseRepository repository;
    @Override
    public LocalCurrencyFranchise find(Long registerNumber) {
        return repository.findById(registerNumber).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Page<LocalCurrencyFranchise> find(List<Integer> sectorCode, Pageable pageable) {
        return repository.findBySectorCodeIn(sectorCode, pageable);
    }

    @Override
    public List<LocalCurrencyFranchise> find(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude) {
        return repository.findAll(LocalCurrencyFranchiseSpecification.inside(fromLatitude, toLatitude, fromLongitude, toLongitude));
    }

    @Override
    public Page<LocalCurrencyFranchise> find(String cityName, Pageable pageable) {
        return repository.findByCityNameContaining(cityName, pageable);
    }
}
