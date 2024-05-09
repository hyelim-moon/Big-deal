package com.capstone.dto;

import com.capstone.entity.Franchise;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FranchiseResponse {
    private final String uuid;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final Boolean paper;
    private final Boolean mobile;
    private final Boolean card;

    private final String name;
    private final String mapAddress;
    public FranchiseResponse(Franchise franchise) {
        this.uuid = franchise.getUuid();
        this.latitude = franchise.getLatitude();
        this.longitude = franchise.getLongitude();
        this.paper = franchise.getPaper();
        this.mobile = franchise.getMobile();
        this.card = franchise.getCard();

        this.name = franchise.getName();
        this.mapAddress = franchise.getMapAddress();

    }
}
