package com.capstone.dto;

import com.capstone.entity.Franchise;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FranchiseInfoResponse {
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final Boolean paper;
    private final Boolean mobile;
    private final Boolean card;
    private final String name;
    private final String phoneNumber;
    private final Integer postNumber;
    private final String roadAddress;
    private final String mapAddress;
    private final Integer state;
    private final String specificInfo;
    public FranchiseInfoResponse(Franchise franchise) {
        this.latitude = franchise.getLatitude();
        this.longitude = franchise.getLongitude();
        this.paper = franchise.getPaper();
        this.mobile = franchise.getMobile();
        this.card = franchise.getCard();
        this.name = franchise.getName();
        this.phoneNumber = franchise.getPhoneNumber();
        this.postNumber = franchise.getPostNumber();
        this.roadAddress = franchise.getRoadAddress();
        this.mapAddress = franchise.getMapAddress();
        this.state = franchise.getState();
        this.specificInfo = franchise.getSpecificInfo();
    }
}
