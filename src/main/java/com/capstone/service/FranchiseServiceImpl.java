package com.capstone.service;

import com.capstone.entity.Franchise;
import com.capstone.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("localCurrencyFranchiseServiceImpl")
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseService {
    private final FranchiseRepository repository;
    @Override
    public Franchise find(Long registerNumber) {
        return repository.findById(registerNumber).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Page<Franchise> findBySectorCodeIn(List<Integer> sectorCode, Pageable pageRequest) {
        return repository.findBySectorCodeIn(sectorCode, pageRequest);
    }

    @Override
    public List<Franchise> findByLatitudeAndLongitude(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude) {
        BigDecimal mediumLatitude = toLatitude.subtract(fromLatitude).divide(BigDecimal.valueOf(2));
        BigDecimal mediumLongitude = toLongitude.subtract(fromLongitude).divide(BigDecimal.valueOf(2));
        List<Franchise> list = repository.findByLatitudeBetweenAndLongitudeBetween(fromLatitude, toLatitude, fromLongitude, toLongitude);
        list.sort((o1, o2) -> {
            BigDecimal len1 = o1.getLatitude().subtract(mediumLatitude).pow(2).add(o1.getLongitude().subtract(mediumLongitude).pow(2));
            BigDecimal len2 = o2.getLatitude().subtract(mediumLatitude).pow(2).add(o2.getLongitude().subtract(mediumLongitude).pow(2));
            return len2.compareTo(len1);
        });
        return list;
    }

    @Override
    public Page<Franchise> findByCityName(String cityName, Pageable pageRequest) {
        return repository.findByCityNameContaining(cityName, pageRequest);
    }
}
