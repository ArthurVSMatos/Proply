package com.proply.features.auth.controller;

import com.proply.features.auth.dto.LoginRequestDTO;
import com.proply.features.auth.dto.LoginResponseDTO;
import com.proply.features.auth.dto.RegisterRequestDTO;
import com.proply.features.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public void register(@RequestBody @Valid RegisterRequestDTO dto) {

        service.register(dto);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO dto) {

        String token = service.login(dto);
        return new LoginResponseDTO(token);
    }
}