package com.capstone.repository;

import com.capstone.entity.LocalCurrencyFranchise;
import com.capstone.entity.LocalCurrencyFranchiseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.jpa.JpaSystemException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
        Assertions.assertEquals(2, francList.size());
    }
    @Test
    public void findByPoint() {
        List<LocalCurrencyFranchise> franchisesList = repository.findAll(LocalCurrencyFranchiseSpecification.inside(new BigDecimal("37.64373254"), new BigDecimal("127.1415573"), new BigDecimal("37.64373254"), new BigDecimal("127.1415573")));
        Assertions.assertEquals(1, franchisesList.size());
        franchisesList = repository.findAll(LocalCurrencyFranchiseSpecification.inside(new BigDecimal("37.59"), new BigDecimal("0"), new BigDecimal("37.6"), new BigDecimal("150")));
        Assertions.assertEquals(6, franchisesList.size());
    }
    @Test
    public void nullableTest() {
        Assertions.assertThrows(JpaSystemException.class, () -> repository.save(new LocalCurrencyFranchise()));
        Assertions.assertThrows(RuntimeException.class, () -> repository.save(new LocalCurrencyFranchise(1L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)));
    }
}
