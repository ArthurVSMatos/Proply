package com.proply.features.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(

        @NotBlank
        String name,

        @Email
        String email,

        @NotBlank
        String password

) {
}