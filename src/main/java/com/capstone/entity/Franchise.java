package com.capstone.entity;

import com.capstone.dto.FranchiseResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Franchise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private String uuid;
    @Column
    private Long registerNumber;
    @Column
    private Long franchiseNumber;
    @Column
    private Integer sectorCode;
    @Column
    private String sector;
    @Column
    private String cityName;
    @Column(precision=38,scale=30)
    private BigDecimal latitude;
    @Column(precision=38,scale=30)
    private BigDecimal longitude;
    @Column
    private Boolean paper;
    @Column
    private Boolean mobile;
    @Column
    private Boolean card;
    @Column
    private String phoneNumber;
    @Column
    private String name;
    @Column
    private Integer postNumber;
    @Column
    private String roadAddress;
    @Column
    private String mapAddress;
    @Column
    private Integer state;
    @Column
    private String specificInfo;
    @Builder
    public Franchise(Long registerNumber, Long franchiseNumber, Integer sectorCode, String sector, String cityName, BigDecimal latitude, BigDecimal longitude, Boolean paper, Boolean mobile, Boolean card, String phoneNumber, String name, Integer postNumber, String roadAddress, String mapAddress, Integer state) {
        this.registerNumber = registerNumber;
        this.franchiseNumber = franchiseNumber;
        this.sectorCode = sectorCode;
        this.sector = sector;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.paper = paper;
        this.mobile = mobile;
        this.card = card;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.postNumber = postNumber;
        this.roadAddress = roadAddress;
        this.mapAddress = mapAddress;
        this.state = state;
    }
}
