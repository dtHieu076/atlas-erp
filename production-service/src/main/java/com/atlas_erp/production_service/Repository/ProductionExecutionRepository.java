package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.ProductionExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductionExecutionRepository extends JpaRepository<ProductionExecutionEntity, UUID> {

    List<ProductionExecutionEntity> findByProductionOrderId(UUID productionOrderId);

    Optional<ProductionExecutionEntity> findByProductionOrderIdAndEndTimeIsNull(
            UUID productionOrderId);
}
