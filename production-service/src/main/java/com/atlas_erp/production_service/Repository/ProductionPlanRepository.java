package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.ProductionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlanEntity, UUID> {

    Optional<ProductionPlanEntity> findByPlanCode(String planCode);

    boolean existsByPlanCode(String planCode);
}
