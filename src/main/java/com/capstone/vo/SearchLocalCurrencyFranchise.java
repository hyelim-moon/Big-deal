package com.capstone.vo;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class SearchLocalCurrencyFranchise {
    private final String name;
    private final List<Integer> sectorCodes;
    private final String cityName;
    private final BigDecimal fromLatitude;
    private final BigDecimal fromLongitude;
    private final BigDecimal toLatitude;
    private final BigDecimal toLongitude;
    private final Boolean paper;
    private final Boolean mobile;
    private final Boolean card;
    private final Integer state;
    private SearchLocalCurrencyFranchise(LocalCurrencyFranchiseSearchBuilder builder) {
        this.name = builder.name;
        this.sectorCodes = builder.sectorCodes;
        this.cityName = builder.cityName;
        this.fromLatitude = builder.fromLatitude;
        this.toLatitude = builder.toLatitude;
        this.fromLongitude = builder.fromLongitude;
        this.toLongitude = builder.toLongitude;
        this.paper = builder.paper;
        this.mobile = builder.mobile;
        this.card = builder.card;
        this.state = builder.state;
    }
    public static class LocalCurrencyFranchiseSearchBuilder {
        private String name;
        private List<Integer> sectorCodes;
        private String cityName;
        private BigDecimal fromLatitude;
        private BigDecimal fromLongitude;
        private BigDecimal toLatitude;
        private BigDecimal toLongitude;
        private Boolean paper;
        private Boolean mobile;
        private Boolean card;
        private Integer state;
        public LocalCurrencyFranchiseSearchBuilder() {}
        public LocalCurrencyFranchiseSearchBuilder containName(String name) {
            this.name = name;
            return this;
        }
        public LocalCurrencyFranchiseSearchBuilder filterSectorCode(List<Integer> sectorCodes) {
            this.sectorCodes = sectorCodes;
            return this;
        }
        public LocalCurrencyFranchiseSearchBuilder containCityName(String cityName) {
            this.cityName = cityName;
            return this;
        }
        public LocalCurrencyFranchiseSearchBuilder betweenLatitude(BigDecimal fromLatitude, BigDecimal toLatitude) {
            this.fromLatitude = fromLatitude;
            this.toLatitude = toLatitude;
            return this;
        }
        public LocalCurrencyFranchiseSearchBuilder betweenLongitude(BigDecimal fromLongitude, BigDecimal toLongitude) {
            this.fromLongitude = fromLongitude;
            this.toLongitude = toLongitude;
            return this;
        }
        public LocalCurrencyFranchiseSearchBuilder withPayCase(Boolean paper, Boolean mobile, Boolean card) {
            this.paper = paper;
            this.mobile = mobile;
            this.card = card;
            return this;
        }
        public LocalCurrencyFranchiseSearchBuilder withState(Integer state) {
            this.state = state;
            return this;
        }
        public SearchLocalCurrencyFranchise build() {
            return new SearchLocalCurrencyFranchise(this);
        }
    }
}
