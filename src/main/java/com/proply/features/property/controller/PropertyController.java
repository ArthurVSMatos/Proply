package com.proply.features.property.controller;

import com.proply.features.property.dto.CreatePropertyDTO;
import com.proply.features.property.dto.PropertyResponseDTO;
import com.proply.features.property.dto.UpdatePropertyDTO;
import com.proply.features.property.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    // ✅ CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponseDTO create(@RequestBody @Valid CreatePropertyDTO dto) {
        return propertyService.create(dto);
    }

    // ✅ GET ALL (com paginação)
    @GetMapping
    public Page<PropertyResponseDTO> list(Pageable pageable) {
        return propertyService.list(pageable);
    }

    // ✅ GET BY ID (seguro multi-tenant)
    @GetMapping("/{id}")
    public PropertyResponseDTO getById(@PathVariable UUID id) {
        return propertyService.getById(id);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public PropertyResponseDTO update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePropertyDTO dto
    ) {
        return propertyService.update(id, dto);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        propertyService.delete(id);
    }
}