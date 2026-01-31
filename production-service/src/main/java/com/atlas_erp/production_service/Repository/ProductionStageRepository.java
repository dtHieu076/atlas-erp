package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.ProductionStageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductionStageRepository extends JpaRepository<ProductionStageEntity, UUID> {

    Optional<ProductionStageEntity> findByCode(String code);
}
