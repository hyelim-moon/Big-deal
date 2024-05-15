package com.capstone.dto.franchise;

import com.capstone.entity.Franchise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddFranchiseRequest {
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
    public Franchise toEntity() {
        return Franchise.builder()
                .registerNumber(registerNumber)
                .franchiseNumber(franchiseNumber)
                .sectorCode(sectorCode)
                .sector(sector)
                .cityName(cityName)
                .latitude(latitude)
                .longitude(longitude)
                .paper(paper)
                .mobile(mobile)
                .card(card)
                .phoneNumber(phoneNumber)
                .name(name)
                .postNumber(postNumber)
                .roadAddress(roadAddress)
                .mapAddress(mapAddress)
                .state(state)
                .build();
    }
}
