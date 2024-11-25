package com.capstone.repository;

import com.capstone.entity.Franchiseinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FranchiseInfoRepository extends JpaRepository<Franchiseinfo, Long> {
    List<Franchiseinfo> findAll();

}
