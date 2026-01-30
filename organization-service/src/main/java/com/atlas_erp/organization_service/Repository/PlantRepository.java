package com.atlas_erp.organization_service.Repository;

import com.atlas_erp.organization_service.Entity.PlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlantRepository extends JpaRepository<PlantEntity, UUID> {
}
