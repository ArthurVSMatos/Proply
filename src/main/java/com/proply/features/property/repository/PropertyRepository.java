package com.proply.features.property.repository;

import com.proply.features.property.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    // ✅ AGORA SIM! Devolve uma Página em vez de uma Lista
    Page<Property> findAllByCompanyId(UUID companyId, Pageable pageable);

    // ... o resto continua igual
    Optional<Property> findByIdAndCompanyId(UUID id, UUID companyId);
}

