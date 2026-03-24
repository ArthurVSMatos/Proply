package com.proply.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record RegisterRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        // 👈 Removido o @NotBlank para permitir usuários em empresas existentes
        String companyName,

        // 👈 Adicionado para vincular a uma empresa que já existe
        UUID companyId
) {}