package com.capstone.service;

import com.capstone.entity.Franchise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FranchiseServiceTest {
    @Autowired
    private FranchiseService service;
    @DisplayName("success")
    @Test
    public void getTest() {
        Franchise f1 = service.find(2230441596L);
    }
    @DisplayName("exception")
    @Test
    public void errorTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {service.find(1L);});
    }
    @Test
    public void findBySectorCode() {
        List<Integer> sectorCodeList = new ArrayList<>();
        sectorCodeList.add(2310);
        Page<Franchise> page = service.findBySectorCodeIn(sectorCodeList, Pageable.ofSize(10));
        for (Franchise f : page) {
            Assertions.assertTrue(sectorCodeList.contains(f.getSectorCode()));
        }
    }
}
