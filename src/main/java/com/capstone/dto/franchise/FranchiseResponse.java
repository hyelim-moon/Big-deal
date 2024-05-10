package com.capstone.dto.franchise;

import com.capstone.entity.Franchise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class FranchiseResponse {
    private final String uuid;
    private final Long registerNumber;
    private final Long franchiseNumber;
    private final Integer sectorCode;
    private final String sector;
    private final String cityName;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final Boolean paper;
    private final Boolean mobile;
    private final Boolean card;
    private final String phoneNumber;
    private final String name;
    private final Integer postNumber;
    private final String roadAddress;
    private final String mapAddress;
    private final Integer state;
    private final String specificInfo;
    public FranchiseResponse(Franchise franchise) {
        this.uuid = franchise.getUuid();
        this.registerNumber = franchise.getRegisterNumber();
        this.franchiseNumber = franchise.getFranchiseNumber();
        this.sectorCode = franchise.getSectorCode();
        this.sector = franchise.getSector();
        this.cityName = franchise.getCityName();
        this.latitude = franchise.getLatitude();
        this.longitude = franchise.getLongitude();
        this.paper = franchise.getPaper();
        this.mobile = franchise.getMobile();
        this.card = franchise.getCard();
        this.phoneNumber = franchise.getPhoneNumber();
        this.name = franchise.getName();
        this.postNumber = franchise.getPostNumber();
        this.roadAddress = franchise.getRoadAddress();
        this.mapAddress = franchise.getMapAddress();
        this.state = franchise.getState();
        this.specificInfo = franchise.getSpecificInfo();
    }
}
