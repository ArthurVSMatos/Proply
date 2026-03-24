package com.proply.features.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PropertyRequestDTO(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull BigDecimal price,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String address,
        @NotBlank String type // HOUSE, APARTMENT, etc.
) {
}