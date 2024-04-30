package com.capstone.repository;

import com.capstone.entity.LocalCurrencyFranchise;
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
public class LocalCurrencyFranchiseRepositoryTest {
    @Autowired
    private LocalCurrencyFranchiseRepository repository;
    @BeforeEach
    public void dataInit() {
        repository.deleteAll();
        LocalCurrencyFranchise[] franchises = {
                new LocalCurrencyFranchise(2230441596L,967290149L,9602,"기타서비스","구리시",new BigDecimal("37.64373254"),new BigDecimal("127.1415573"),false,false,false,"","펄스코(PULSECO)",11906,"경기도 구리시 동구릉로 520-7","경기도 구리시 사노동 452-12번지",1,""),
                new LocalCurrencyFranchise(6032022252L,910202325L,2310,"식음료(기타)","구리시",new BigDecimal("37.5917687"),new BigDecimal("127.150121"),false,false,false,"","안녕마카롱",11945,"경기도 구리시 벌말로 172","경기도 구리시 토평동 996번지 1층",1,""),
                new LocalCurrencyFranchise(3921701304L,903135979L,2301,"일반음식점","구리시",new BigDecimal("37.59726461"),new BigDecimal("127.140353"),false,false,false,"","형아네순대국",11933,"경기도 구리시 안골로 82-1","경기도 구리시 수택동 431-10번지",1,""),
                new LocalCurrencyFranchise(1321972763L,975263807L,8112,"기타상품판매점","구리시",new BigDecimal("37.59609497"),new BigDecimal("127.1410497"),false,false,false,"","오벨전자담배(수택점)",11933,"경기도 구리시 원수택로 45-1","경기도 구리시 수택동 436-22번지 1층 이레컴퓨터",1,""),
                new LocalCurrencyFranchise(1329270244L,989627880L,2310,"식음료(기타)","구리시",new BigDecimal("37.59872584"),new BigDecimal("127.1406972"),false,false,false,"","소망식품",11928,"경기도 구리시 안골로97번길 33-7","경기도 구리시 수택동 419-3번지 1층",1,""),
                new LocalCurrencyFranchise(6231001155L,995800711L,1106,"일반의류","구리시",new BigDecimal("37.59262791"),new BigDecimal("127.139118"),false,false,false,"","원더우먼",11933,"경기도 구리시 원수택로 1","경기도 구리시 수택동 755-8번지",1,""),
                new LocalCurrencyFranchise(1513801334L,916539878L,2105,"기타식음료품","구리시",new BigDecimal("37.59072032"),new BigDecimal("127.1343245"),false,false,false,"","달달구리 덕현점",11938,"경기도 구리시 장자대로37번길 61","경기도 구리시 교문동 808번지 덕현아파트 1층 115호 일부",1,""),
                new LocalCurrencyFranchise(3126400297L,990700189L,2301,"일반음식점","군포시",new BigDecimal("37.35096998"),new BigDecimal("126.9434422"),false,false,false,"","옛날칼국수",15859,"경기도 군포시 당동로 10","경기도 군포시 당동 771-3번지 1층",1,""),
                new LocalCurrencyFranchise(2645400515L,912731289L,1602,"미용실(두발전문)","군포시",new BigDecimal("37.33145704"),new BigDecimal("126.9157951"),false,false,false,"","이솔헤어",15886,"경기도 군포시 대야2로143번길 25-1","경기도 군포시 대야미동 636-3번지 센트럴아이파크 상가 1층 110호",1,""),
                new LocalCurrencyFranchise(4146500095L,988296109L,2301,"일반음식점","구리시",new BigDecimal("37.58471573"),new BigDecimal("127.136721"),false,false,false,"","장자못궁중삼계탕",11959,"경기도 구리시 장자호수길 62-4","경기도 구리시 교문동 543-21번지 에이동. 1층",1,""),
                new LocalCurrencyFranchise(1171426149L,969646116L,2301,"일반음식점","군포시",new BigDecimal("37.35946101"),new BigDecimal("126.9302015"),false,false,false,"","홍두깨손칼국수",15865,"경기도 군포시 광정로 70","경기도 군포시 산본동 1144-1번지 유공빌딩 1층 105호",1,"")
        };
        repository.saveAll(Arrays.stream(franchises).toList());
    }
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
        Assertions.assertFalse(francList.isEmpty());
        for(LocalCurrencyFranchise franchise : francList) {
            Assertions.assertTrue(list.contains(franchise.getSectorCode()));
        }
    }
    @Test
    public void findBetween() {
        checkPosition(repository.findByLatitudeBetweenAndLongitudeBetween(new BigDecimal("37.59"),new BigDecimal("37.60"),new BigDecimal("127.13"),new BigDecimal("127.16")),"37.59","37.60","127.13","127.16");
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
