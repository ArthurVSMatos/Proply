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
    // Construtor compacto para converter a Entity direto para DTO
    public PropertyResponseDTO(com.proply.features.property.entity.Property p) {
        this(
                p.getId(),
                p.getTitle(),
                p.getPrice(),
                p.getCity(),
                p.getType().toString(),
                p.getStatus().toString(),
                p.getCompany().getName()
        );
    }
}