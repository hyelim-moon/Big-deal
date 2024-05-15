package com.capstone.service;

import com.capstone.dto.franchise.FranchiseResponse;
import com.capstone.entity.Franchise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FranchiseServiceTest {
    @Autowired
    private FranchiseService service;
    @DisplayName("success")
    @Test
    public void getTest() {
        FranchiseResponse f1 = service.findById("1");
    }
    @DisplayName("exception")
    @Test
    public void errorTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {service.findById("1L");});
    }
    @Test
    public void findBySectorCode() {
        List<Integer> sectorCodeList = new ArrayList<>();
        sectorCodeList.add(2310);
        Page<Franchise> page = service.findBySectorCodeIn(sectorCodeList, Pageable.ofSize(10));
        Assertions.assertTrue(page.getSize() > 0);
        for (Franchise f : page) {
            Assertions.assertTrue(sectorCodeList.contains(f.getSectorCode()));
        }
    }
    @Test
    public void findByCenterTest() {
        List<FranchiseResponse> list = service.findByCenter(new BigDecimal("33.450700761312206"), new BigDecimal("126.57066121198349"));
        BigDecimal prev = list.get(0).getLatitude().subtract(new BigDecimal("33.450700761312206")).pow(2).add(list.get(0).getLongitude().subtract(new BigDecimal("126.57066121198349")).pow(2));
        Assertions.assertFalse(list.isEmpty());
        for (int i = 1; i < list.size(); i++) {
            FranchiseResponse f = list.get(i);
            BigDecimal curr = f.getLatitude().subtract(new BigDecimal("33.450700761312206")).pow(2).add(f.getLongitude().subtract(new BigDecimal("126.57066121198349")).pow(2));
            Assertions.assertTrue(curr.compareTo(prev) >= 0);
            prev = curr;
        }
    }
    @Test
    public void findByCityNameTest() {
        String cityName = "구리";
        Page<Franchise> page = service.findByCityName(cityName, Pageable.ofSize(10).withPage(0));
        Assertions.assertTrue(page.getSize() > 0);
        for (Franchise franchise : page) {
            Assertions.assertTrue(franchise.getCityName().contains(cityName));
        }
    }
}
