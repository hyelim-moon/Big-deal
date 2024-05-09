package com.capstone.repository;

import com.capstone.entity.Franchise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
public class FranchiseRepositoryTest {
    @Autowired
    private FranchiseRepository repository;
    @Test
    public void getData() {

        Franchise franc = repository.findById("0").orElseThrow(IllegalArgumentException::new);
        Assertions.assertEquals("펄스코(PULSECO)", franc.getName());

        List<Franchise> francList = repository.findAll(Pageable.ofSize(2).withPage(1)).toList();
        Assertions.assertEquals(2, francList.size());
        List<Franchise> francList2 = repository.findAll(Pageable.ofSize(2).withPage(2)).toList();
        Assertions.assertEquals(2, francList2.size());
        Assertions.assertNotEquals(francList, francList2);
    }
    @Test
    public void findBySectorCode() {
        List<Integer> list = new ArrayList<Integer>();
        List<Franchise> francList = repository.findBySectorCodeIn(list, Pageable.ofSize(5)).toList();
        Assertions.assertTrue(francList.isEmpty());

        list.add(2310);
        francList = repository.findBySectorCodeIn(list, Pageable.ofSize(10)).toList();
        Assertions.assertFalse(francList.isEmpty());
        for(Franchise franchise : francList) {
            Assertions.assertTrue(list.contains(franchise.getSectorCode()));
        }
    }
    @Test
    public void findBetween() {
        checkPosition(repository.findByLatitudeBetweenAndLongitudeBetween(new BigDecimal("37.59"),new BigDecimal("37.60"),new BigDecimal("127.13"),new BigDecimal("127.16")),"37.59","37.60","127.13","127.16");
    }
    private void checkPosition(List<Franchise> franchisesList, String fromLatitudeString, String toLatitudeString, String fromLongitudeString, String toLongitudeString) {
        BigDecimal fromLatitude, toLatitude, fromLongitude, toLongitude;
        fromLatitude = new BigDecimal(fromLatitudeString);
        toLatitude = new BigDecimal(toLatitudeString);
        fromLongitude = new BigDecimal(fromLongitudeString);
        toLongitude = new BigDecimal(toLongitudeString);
        for (Franchise franchise : franchisesList) {
            Assertions.assertTrue(franchise.getLatitude().compareTo(fromLatitude) >= 0 && franchise.getLatitude().compareTo(toLatitude) <= 0);
            Assertions.assertTrue(franchise.getLongitude().compareTo(fromLongitude) >= 0 && franchise.getLongitude().compareTo(toLongitude) <= 0);
        }
    }
    @Test
    public void nullableTest() {  // What do we need to result this case?
        Assertions.assertThrows(JpaSystemException.class, () -> repository.save(new Franchise()));
        Assertions.assertThrows(RuntimeException.class, () -> repository.save(new Franchise("",null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)));
    }
}
