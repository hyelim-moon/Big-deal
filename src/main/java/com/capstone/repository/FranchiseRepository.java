package com.capstone.repository;

import com.capstone.entity.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, String>, JpaSpecificationExecutor<Franchise> {
    Page<Franchise> findBySectorCodeIn(Collection<Integer> sectorCodes, Pageable pageable);
    Page<Franchise> findByCityNameContaining(String cityName, Pageable pageable);
    List<Franchise> findByLatitudeBetweenAndLongitudeBetween(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude);
    List<Franchise> findByLatitudeBetweenAndLongitudeBetweenAndSectorCodeIn(BigDecimal fromLatitude, BigDecimal toLatitude, BigDecimal fromLongitude, BigDecimal toLongitude, List<Integer> sectorCode);
    Page<Franchise> findByCityNameContainingAndNameContaining(String cityName, String name, Pageable pageable);
}
