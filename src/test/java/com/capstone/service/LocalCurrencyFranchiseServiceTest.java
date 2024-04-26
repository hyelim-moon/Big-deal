package com.capstone.service;

import com.capstone.entity.LocalCurrencyFranchise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class LocalCurrencyFranchiseServiceTest {
    @Autowired
    private LocalCurrencyFranchiseService service;
    @DisplayName("sucesse")
    @Test
    public void getTest() {
        LocalCurrencyFranchise f1 = service.find(2230441596L);
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
        Page<LocalCurrencyFranchise> page = service.findBySectorCodeIn(sectorCodeList, Pageable.ofSize(10));
        for (LocalCurrencyFranchise f : page) {
            Assertions.assertTrue(sectorCodeList.contains(f.getSectorCode()));
        }
    }
}
