package com.capstone.service;

import com.capstone.entity.LocalCurrencyFranchise;
import com.capstone.repository.LocalCurrencyFranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<LocalCurrencyFranchise> findBySectorCodeIn(List<Integer> sectorCode, Pageable pageRequest) {
        return repository.findBySectorCodeIn(sectorCode, pageRequest);
    }

    @Override
    public List<LocalCurrencyFranchise> findByLatitudeAndLongitude(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude) {
        BigDecimal mediumLatitude = toLatitude.subtract(fromLatitude).divide(BigDecimal.valueOf(2));
        BigDecimal mediumLongitude = toLongitude.subtract(fromLongitude).divide(BigDecimal.valueOf(2));
        List<LocalCurrencyFranchise> list = repository.findAll();
        list.sort((o1, o2) -> {
            BigDecimal len1 = o1.getLatitude().subtract(mediumLatitude).pow(2).add(o1.getLongitude().subtract(mediumLongitude).pow(2));
            BigDecimal len2 = o2.getLatitude().subtract(mediumLatitude).pow(2).add(o2.getLongitude().subtract(mediumLongitude).pow(2));
            return len2.compareTo(len1);
        });
        return list;
    }

    @Override
    public Page<LocalCurrencyFranchise> findByCityName(String cityName, Pageable pageRequest) {
        return repository.findByCityNameContaining(cityName, pageRequest);
    }
}
