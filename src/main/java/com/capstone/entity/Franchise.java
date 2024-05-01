package com.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Franchise {
    @Id
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
}
