package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.ProductionOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import java.util.List;

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

        @Query("SELECT p FROM ProductionOrderEntity p WHERE " +
                        "(:status IS NULL OR p.status = :status) AND " +
                        "(:planId IS NULL OR p.productionPlanId = :planId)")
        Page<ProductionOrderEntity> findByFilters(
                        @Param("status") String status,
                        @Param("planId") UUID planId,
                        Pageable pageable);// Thêm phương thức này để lấy toàn bộ danh sách Order của 1 Plan (không phân
                                           // trang)

        List<ProductionOrderEntity> findByProductionPlanId(UUID productionPlanId);
}
