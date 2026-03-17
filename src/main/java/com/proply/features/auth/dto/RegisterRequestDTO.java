package com.proply.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(

        @NotBlank (message = "Name is required")
        String name,

        @NotBlank (message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank (message = "Password is required")
        String password,

        @NotBlank (message = "Company Name is required")
        String companyName

) {}