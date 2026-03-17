package com.proply.features.property.controller;

import com.proply.features.property.dto.CreatePropertyDTO;
import com.proply.features.property.dto.PropertyResponseDTO;
import com.proply.features.property.entity.Property;
import com.proply.features.property.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devolve 201 em vez de 200
    public PropertyResponseDTO create(@RequestBody @Valid CreatePropertyDTO dto) {

        // 1. A Service cria a propriedade e devolve a entidade completa
        Property savedProperty = propertyService.create(dto);

        // 2. Mapeamos a Entidade para o teu DTO seguro
        return new PropertyResponseDTO(
                savedProperty.getId(),
                savedProperty.getTitle(),
                savedProperty.getPrice(),
                savedProperty.getCity(),
                savedProperty.getType().name(),   // Usa .name() se type for um Enum
                savedProperty.getStatus().name(), // Usa .name() se status for um Enum
                savedProperty.getCompany().getName() // Vazamento resolvido! Pegamos só o nome.
        );
    }

    @GetMapping
    public List<PropertyResponseDTO> list() {
        return propertyService.list();
    }
}