package com.proply.features.property.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PropertyResponseDTO(
        UUID id,
        String title,
        BigDecimal price,
        String city,
        String type,
        String status,
        String companyName // Apenas o nome, não o objeto inteiro!
) {
}