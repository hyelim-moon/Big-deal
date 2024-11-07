package com.capstone.repository;

import com.capstone.entity.NewFranchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewFranchiseRepository extends JpaRepository<NewFranchise, Long> {
    boolean existsByNameAndAddress(String name, String address);

}


