package com.proply.features.auth.service;

import com.proply.features.auth.dto.RegisterRequestDTO;
import com.proply.features.company.entity.Company;
import com.proply.features.company.repository.CompanyRepository;
import com.proply.features.user.entity.User;
import com.proply.features.user.repository.UserRepository;
import com.proply.shared.exception.BusinessException;
import com.proply.shared.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDTO dto) {


        String slug = SlugUtil.generate(dto.companyName());

        if (companyRepository.findBySlug(slug).isPresent()) {
            throw new BusinessException("Company slug already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException("Email already registered",HttpStatus.BAD_REQUEST);
        }


        Company company = Company.builder()
                .id(UUID.randomUUID())
                .name(dto.companyName())
                .slug(SlugUtil.generate(dto.companyName()))
                .email(dto.email())
                .createdAt(LocalDateTime.now())
                .build();

        companyRepository.save(company);

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role("ADMIN")
                .company(company)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }
}