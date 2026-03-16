package com.proply.features.property.service;

import com.proply.features.property.dto.CreatePropertyDTO;
import com.proply.features.property.dto.PropertyResponseDTO;
import com.proply.features.property.entity.Property;
import com.proply.features.property.repository.PropertyRepository;
import com.proply.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.proply.features.property.enums.PropertyType;
import com.proply.features.property.enums.PropertyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public Property create(CreatePropertyDTO dto) {
        // 1. Recupera o tenant do contexto
        var tenant = TenantContext.getTenant();

        // Validação de segurança: Nunca salvar sem um tenant!
        if (tenant == null) {
            throw new RuntimeException("Tenant context not found. Make sure to provide X-Company-Slug header.");
        }

        // 2. Construção da entidade
        Property property = Property.builder()
                // Removi o UUID.randomUUID() para deixar o @GeneratedValue trabalhar
                .title(dto.title())
                .description(dto.description())
                .price(dto.price())
                .address(dto.address())
                .city(dto.city())
                .state(dto.state())
                .type(PropertyType.valueOf(dto.type().toUpperCase())) // toUpperCase previne erros de digitação
                .status(PropertyStatus.valueOf(dto.status().toUpperCase()))
                .company(tenant) // Aqui injetamos o tenant automaticamente
                .createdAt(LocalDateTime.now())
                .build();

        return propertyRepository.save(property);
    }

    public Property getById(UUID id) {
        var tenant = TenantContext.getTenant();

        // Procuramos a propriedade pelo ID
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // VALIDACÃO DE ISOLAMENTO:
        // Se a empresa da propriedade for diferente da empresa do contexto atual...
        if (!property.getCompany().getId().equals(tenant.getId())) {
            // Bloqueamos o acesso!
            throw new RuntimeException("Acesso negado: Esta propriedade pertence a outra empresa.");
        }

        return property;
    }
    public List<PropertyResponseDTO> list() {
        var tenant = TenantContext.getTenant();
        if (tenant == null) return List.of();

        List<Property> properties = propertyRepository.findByCompanyId(tenant.getId());

        // Convertemos cada Property em PropertyResponseDTO
        return properties.stream()
                .map(p -> new PropertyResponseDTO(
                        p.getId(),
                        p.getTitle(),
                        p.getPrice(),
                        p.getCity(),
                        p.getType().name(),
                        p.getStatus().name(),
                        p.getCompany().getName() // Expondo apenas o nome da empresa
                ))
                .toList();
    }
}