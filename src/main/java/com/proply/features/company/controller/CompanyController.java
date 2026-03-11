package com.proply.features.company.controller;

import com.proply.features.company.dto.CreateCompanyDTO;
import com.proply.features.company.entity.Company;
import com.proply.features.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public Company create(@RequestBody @Valid CreateCompanyDTO dto) {
        return service.createCompany(dto);
    }
}