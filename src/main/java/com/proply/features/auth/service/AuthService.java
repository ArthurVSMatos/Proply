package com.proply.features.auth.service;

import com.proply.config.security.JwtService;
import com.proply.features.auth.dto.LoginRequestDTO;
import com.proply.features.auth.dto.RegisterRequestDTO;
import com.proply.features.company.entity.Company;
import com.proply.features.company.repository.CompanyRepository;
import com.proply.features.user.entity.User;
import com.proply.features.user.repository.UserRepository;
import com.proply.shared.exception.BusinessException;
import com.proply.shared.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;


    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDTO dto) {
        // 1. Validação de E-mail (Sempre universal)
        if (dto.companyId() == null && (dto.companyName() == null || dto.companyName().isBlank())) {
            throw new BusinessException("Either Company ID or Company Name must be provided", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        Company company;

        // 2. BIFURCAÇÃO: Empresa Existente vs Empresa Nova
        if (dto.companyId() != null) {
            // Fluxo: Adicionando usuário a empresa que já existe
            company = companyRepository.findById(dto.companyId())
                    .orElseThrow(() -> new BusinessException("Company not found", HttpStatus.NOT_FOUND));
            log.info("Adicionando novo usuário à empresa existente: {}", company.getName());
        } else {
            // Fluxo Original: Criando uma empresa DO ZERO
            String slug = SlugUtil.generate(dto.companyName());
            if (companyRepository.findBySlug(slug).isPresent()) {
                throw new BusinessException("Company slug already exists", HttpStatus.BAD_REQUEST);
            }

            company = Company.builder()
                    .id(UUID.randomUUID())
                    .name(dto.companyName())
                    .slug(slug)
                    .email(dto.email())
                    .createdAt(LocalDateTime.now())
                    .build();

            companyRepository.save(company);
            log.info("Novo tenant registrado: {}", dto.companyName());
        }

        // 3. Criação do Usuário (O Role pode vir do DTO ou ser USER por padrão se já tem empresa)
        User user = User.builder()
                .id(UUID.randomUUID())
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                // Se ele está criando a empresa, é ADMIN. Se está entrando em uma, é USER.
                .role(dto.companyId() != null ? "USER" : "ADMIN")
                .company(company)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("Usuário registrado com sucesso: {}", dto.email());
    }
    public String login (LoginRequestDTO dto){
        log.info("Attempting login for user: {}", dto.email());

        User user =
    userRepository.findByEmail(dto.email())
            .orElseThrow(() -> new BusinessException("invalid credentials",HttpStatus.UNAUTHORIZED));

        if(!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.warn("Failed login attempt for user: {}", dto.email());
            throw new BusinessException("invalid credentials",HttpStatus.UNAUTHORIZED);

        }
        return
                jwtService.generateToken(user.getEmail());

    }


}