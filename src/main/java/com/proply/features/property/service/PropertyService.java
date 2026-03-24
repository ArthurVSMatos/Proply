package com.proply.features.property.service;

import com.proply.features.property.dto.CreatePropertyDTO;
import com.proply.features.property.dto.PropertyRequestDTO;
import com.proply.features.property.dto.PropertyResponseDTO;
import com.proply.features.property.dto.UpdatePropertyDTO;
import com.proply.features.property.entity.Property;
import com.proply.features.property.repository.PropertyRepository;
import com.proply.shared.exception.BusinessException;
import com.proply.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.proply.features.property.enums.PropertyType;
import com.proply.features.property.enums.PropertyStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyResponseDTO create(PropertyRequestDTO dto) {
        var tenant = TenantContext.getTenant();
        if (tenant == null) {
            throw new BusinessException("Tenant context not found", HttpStatus.NOT_FOUND);
        }

        // 1. Construímos a entidade com o builder
        Property property = Property.builder()
                .title(dto.title())
                .description(dto.description())
                .price(dto.price())
                .address(dto.address())
                .city(dto.city())
                .state(dto.state())
                .type(parseType(dto.type()))
                .status(PropertyStatus.AVAILABLE)
                .company(tenant)
                .createdAt(LocalDateTime.now())
                .build();

        // 2. SALVAMOS APENAS UMA VEZ
        Property savedProperty = propertyRepository.save(property);

        log.info("New property created with ID: {} and Title: {}",
                savedProperty.getId(),
                savedProperty.getTitle());

        // 3. Convertemos o objeto que JÁ VEIO DO BANCO para o DTO de resposta
        return toResponse(savedProperty);
    }

    public PropertyResponseDTO getById(UUID id) {
        var companyId = getCompanyId();

        var property = propertyRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Property not found",HttpStatus.NOT_FOUND));

        return toResponse(property);
    }

    public Page<PropertyResponseDTO> list(Pageable pageable) {
        var companyId = getCompanyId();

        log.info("Buscando todos os imóveis do tenant logado");

        return propertyRepository
                .findAllByCompanyId(companyId, pageable)
                .map(this::toResponse);
    }

    private UUID getCompanyId() {
        var tenant = TenantContext.getTenant();
        if (tenant == null) throw new BusinessException("Tenant context not found", HttpStatus.NOT_FOUND);
        return tenant.getId();
    }

    private PropertyType parseType(String type) {
        try {
            return PropertyType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("Invalid property type", HttpStatus.BAD_REQUEST);
        }
    }

    private PropertyStatus parseStatus(String status) {
        try {
            return PropertyStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("Invalid property status", HttpStatus.BAD_REQUEST);
        }
    }

    private PropertyResponseDTO toResponse(Property p) {
        return new PropertyResponseDTO(
                p.getId(),
                p.getTitle(),
                p.getPrice(),
                p.getCity(),
                p.getType().name(),
                p.getStatus().name(),
                p.getCompany().getName()
        );
    }
    public PropertyResponseDTO update(UUID id, UpdatePropertyDTO dto) {
        var companyId = getCompanyId();

        Property property = propertyRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Property not found", HttpStatus.NOT_FOUND));

        // 🔒 regra de negócio (nível empresa)
        if (property.getStatus() == PropertyStatus.SOLD) {
            throw new BusinessException("Sold properties cannot be updated", HttpStatus.BAD_REQUEST);
        }

        property.setTitle(dto.title());
        property.setDescription(dto.description());
        property.setPrice(dto.price());
        property.setAddress(dto.address());
        property.setCity(dto.city());
        property.setState(dto.state());
        property.setType(parseType(dto.type()));
        property.setStatus(parseStatus(dto.status()));

        return toResponse(propertyRepository.save(property));
    }
    public void delete(UUID id) {
        // 1. Quem é a empresa?
        var companyId = getCompanyId();

        // 2. Tenta encontrar o imóvel dessa empresa
        Property property = propertyRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new RuntimeException("Property not found or access denied"));

        // 3. Se passou pela linha de cima, o imóvel existe e é dele. Pode apagar!
        propertyRepository.delete(property);
    }

}