package com.proply.features.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePropertyDTO(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank (message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        BigDecimal price,

        @NotBlank (message = "Address is required")
        String address,

        @NotBlank (message = "City is required")
        String city,

        @NotBlank (message = "State is required")
        String state,

        String type,
        String status
) {
}