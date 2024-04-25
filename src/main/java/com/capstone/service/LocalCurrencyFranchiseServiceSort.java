package com.capstone.service;

import com.capstone.entity.LocalCurrencyFranchise;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("localCurrencyFranchiseServiceSort")
@Qualifier("localCurrencyFranchiseServiceImpl")
@Primary
@RequiredArgsConstructor
public class LocalCurrencyFranchiseServiceSort implements LocalCurrencyFranchiseService{
    private final LocalCurrencyFranchiseService targetService;
    @Override
    public LocalCurrencyFranchise find(Long registerNumber) {
        return targetService.find(registerNumber);
    }
    @Override
    public Page<LocalCurrencyFranchise> find(List<Integer> sectorCode, Pageable pageable) {
        return targetService.find(sectorCode, pageable);
    }
    @Override
    public List<LocalCurrencyFranchise> find(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude) {
        BigDecimal mediumLatitude = toLatitude.subtract(fromLatitude).divide(BigDecimal.valueOf(2));
        BigDecimal mediumLongitude = toLongitude.subtract(fromLongitude).divide(BigDecimal.valueOf(2));
        List<LocalCurrencyFranchise> list = targetService.find(fromLatitude, toLatitude, fromLongitude, toLongitude);
        list.sort((o1, o2) -> {
            BigDecimal len1 = o1.getLatitude().subtract(mediumLatitude).pow(2).add(o1.getLongitude().subtract(mediumLongitude).pow(2));
            BigDecimal len2 = o2.getLatitude().subtract(mediumLatitude).pow(2).add(o2.getLongitude().subtract(mediumLongitude).pow(2));
            return len2.compareTo(len1);
        });
        return list;
    }
    @Override
    public Page<LocalCurrencyFranchise> find(String cityName, Pageable pageable) {
        return targetService.find(cityName, pageable);
    }
}
