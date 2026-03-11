package com.proply.features.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCompanyDTO(

        @NotBlank
        String name,

        @NotBlank
        String slug,

        @Email
        String email,

        String phone
) {
}