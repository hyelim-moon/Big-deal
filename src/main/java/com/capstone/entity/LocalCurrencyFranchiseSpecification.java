package com.capstone.entity;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class LocalCurrencyFranchiseSpecification {
    public static Specification<LocalCurrencyFranchise> inside(BigDecimal fromLatitude, BigDecimal fromLongitude, BigDecimal toLatitude, BigDecimal toLongitude) {
        return (root, query, builder) -> {
            Predicate latitude = builder.between(root.get("latitude"), fromLatitude, toLatitude);
            Predicate longitude = builder.between(root.get("longitude"), fromLongitude, toLongitude);
            return builder.and(latitude, longitude);
        };
    }
}
