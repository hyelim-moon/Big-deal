package com.capstone.dto.franchise;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewFranchiseResponse {
    private Long id;
    private String name;
    private String address;
    private String sector;
    private Double latitude;
    private Double longitude;
    private List<String> currencies;
    private String createdAt;
}
