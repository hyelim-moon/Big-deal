package com.capstone.dto.franchise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateFranchiseRequest {
    private Long registerNumber;
    private Long franchiseNumber;
    private Integer sectorCode;
    private String sector;
    private String cityName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean paper;
    private Boolean mobile;
    private Boolean card;
    private String phoneNumber;
    private String name;
    private Integer postNumber;
    private String roadAddress;
    private String mapAddress;
    private Integer state;
}
