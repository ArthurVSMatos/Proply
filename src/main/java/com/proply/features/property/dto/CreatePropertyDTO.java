package com.proply.features.property.dto;

import java.math.BigDecimal;

public record CreatePropertyDTO(
        String title,
        String description,
        BigDecimal price,
        String address,
        String city,
        String state,
        String type,
        String status
) {
}