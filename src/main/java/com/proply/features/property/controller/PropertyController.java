package com.proply.features.property.controller;

import com.proply.features.property.dto.CreatePropertyDTO;
import com.proply.features.property.dto.PropertyResponseDTO;
import com.proply.features.property.entity.Property;
import com.proply.features.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public Property create(@RequestBody CreatePropertyDTO dto) {
        return propertyService.create(dto);
    }

    @GetMapping
    public List<PropertyResponseDTO> list() {
        return propertyService.list();
    }
}