package com.capstone.repository;

import com.capstone.entity.LocalCurrencyFranchise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class LocalCurrencyFranchiseRepositoryTest {
    @Autowired
    private LocalCurrencyFranchiseRepository repository;
    @Test
    public void getData() {
        LocalCurrencyFranchise franc = repository.findById(2230441596L).orElseThrow(IllegalArgumentException::new);
        Assertions.assertEquals("펄스코(PULSECO)", franc.getName());

        List<LocalCurrencyFranchise> francList = repository.findAll(Pageable.ofSize(2).withPage(1)).toList();
        Assertions.assertEquals(2, francList.size());
        List<LocalCurrencyFranchise> francList2 = repository.findAll(Pageable.ofSize(2).withPage(2)).toList();
        Assertions.assertEquals(2, francList2.size());
        Assertions.assertNotEquals(francList, francList2);
    }
    @Test
    public void findBySectorCode() {
        List<Integer> list = new ArrayList<Integer>();
        List<LocalCurrencyFranchise> francList = repository.findBySectorCodeIn(list, Pageable.ofSize(5)).toList();
        Assertions.assertTrue(francList.isEmpty());

        list.add(2310);
        francList = repository.findBySectorCodeIn(list, Pageable.ofSize(10)).toList();
        for(LocalCurrencyFranchise franchise : francList) {
            Assertions.assertTrue(list.contains(franchise.getSectorCode()));
        }
    }
    private void checkPosition(List<LocalCurrencyFranchise> franchisesList, String fromLatitudeString, String toLatitudeString, String fromLongitudeString, String toLongitudeString) {
        BigDecimal fromLatitude, toLatitude, fromLongitude, toLongitude;
        fromLatitude = new BigDecimal(fromLatitudeString);
        toLatitude = new BigDecimal(toLatitudeString);
        fromLongitude = new BigDecimal(fromLongitudeString);
        toLongitude = new BigDecimal(toLongitudeString);
        for (LocalCurrencyFranchise franchise : franchisesList) {
            Assertions.assertTrue(franchise.getLatitude().compareTo(fromLatitude) >= 0 && franchise.getLatitude().compareTo(toLatitude) <= 0);
            Assertions.assertTrue(franchise.getLongitude().compareTo(fromLongitude) >= 0 && franchise.getLongitude().compareTo(toLongitude) <= 0);
        }
    }
    @Test
    public void nullableTest() {
        Assertions.assertThrows(JpaSystemException.class, () -> repository.save(new LocalCurrencyFranchise()));
        Assertions.assertThrows(RuntimeException.class, () -> repository.save(new LocalCurrencyFranchise(1L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)));
    }
}
