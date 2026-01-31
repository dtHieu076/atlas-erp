package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.ProductionOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, UUID> {

    Page<ProductionOrderEntity> findByStatus(
            String status,
            Pageable pageable);

    Page<ProductionOrderEntity> findByProductionPlanId(
            UUID productionPlanId,
            Pageable pageable);

    Page<ProductionOrderEntity> findByProductionPlanIdAndStatus(
            UUID productionPlanId,
            String status,
            Pageable pageable);

    Page<ProductionOrderEntity> findByProductionPlanIdAndTeamId(
            UUID productionPlanId,
            UUID teamId,
            Pageable pageable);
}
