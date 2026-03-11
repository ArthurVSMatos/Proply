package com.proply.features.company.service;

import com.proply.features.company.dto.CreateCompanyDTO;
import com.proply.features.company.entity.Company;
import com.proply.features.company.repository.CompanyRepository;
import com.proply.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repository;

    public Company createCompany(CreateCompanyDTO dto) {


        if(repository.findBySlug(dto.slug()).isPresent()){
            throw new BusinessException("Slug already exists");
        }

        if(repository.findByEmail(dto.email()).isPresent()){
            throw new BusinessException("Email already exists");
        }


        Company company = Company.builder()
                .id(UUID.randomUUID())
                .name(dto.name())
                .slug(dto.slug())
                .email(dto.email())
                .phone(dto.phone())
                .plan("FREE")
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(company);
    }
}