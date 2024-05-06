package com.capstone.service;

import com.capstone.dto.FranchiseResponse;
import com.capstone.entity.Franchise;
import com.capstone.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Service("franchiseServiceImpl")
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseService {
    private final FranchiseRepository repository;
    private Comparator<Franchise> sortStrategy(BigDecimal latitude, BigDecimal longitude) {
        return ((o1, o2) -> {
            BigDecimal len1 = o1.getLatitude().subtract(latitude).pow(2).add(o1.getLongitude().subtract(longitude).pow(2));
            BigDecimal len2 = o2.getLatitude().subtract(latitude).pow(2).add(o2.getLongitude().subtract(longitude).pow(2));
            return len1.compareTo(len2); // length : o1 > o2
        });
    }
    @Override
    public Franchise findById(String uuid) {
        return repository.findById(uuid).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Page<Franchise> findBySectorCodeIn(List<Integer> sectorCode, Pageable pageRequest) {
        return repository.findBySectorCodeIn(sectorCode, pageRequest);
    }

    @Override
    public List<Franchise> findByLatitudeAndLongitude(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude) {
        BigDecimal mediumLatitude = toLatitude.subtract(fromLatitude).divide(BigDecimal.valueOf(2));
        BigDecimal mediumLongitude = toLongitude.subtract(fromLongitude).divide(BigDecimal.valueOf(2));
        return repository.findByLatitudeBetweenAndLongitudeBetween(fromLatitude, toLatitude, fromLongitude, toLongitude).stream().sorted(sortStrategy(mediumLatitude, mediumLongitude)).toList();
    }

    @Override
    public List<FranchiseResponse> findByCenter(BigDecimal latitude, BigDecimal longitude) {
        return repository.findAll().stream().filter(franchise -> franchise.getLatitude() != null && franchise.getLongitude() != null).sorted(sortStrategy(latitude, longitude)).map(FranchiseResponse::new).toList();
    }

    @Override
    public Page<Franchise> findByCityName(String cityName, Pageable pageRequest) {
        return repository.findByCityNameContaining(cityName, pageRequest);
    }
}
