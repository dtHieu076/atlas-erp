package com.atlas_erp.organization_service.Repository;

import com.atlas_erp.organization_service.Entity.PlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface PlantRepository extends JpaRepository<PlantEntity, UUID> {
    // Trong PlantRepository.java
    Page<PlantEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    boolean existsByCodeAndOrganizationId(String code, UUID organizationId);
}
