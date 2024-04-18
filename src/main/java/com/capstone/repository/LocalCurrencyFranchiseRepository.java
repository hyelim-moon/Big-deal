package com.capstone.repository;

import com.capstone.entity.LocalCurrencyFranchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface LocalCurrencyFranchiseRepository extends JpaRepository<LocalCurrencyFranchise, Long>, JpaSpecificationExecutor<LocalCurrencyFranchise> {
    Page<LocalCurrencyFranchise> findBySectorCodeIn(Collection<Integer> sectorCodes, Pageable pageable);
    Page<LocalCurrencyFranchise> findByCityNameContaining(String cityName, Pageable pageable);
    List<LocalCurrencyFranchise> findAll(Specification<LocalCurrencyFranchise> specification);
}
