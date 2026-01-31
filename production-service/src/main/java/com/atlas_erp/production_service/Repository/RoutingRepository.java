package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.RoutingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoutingRepository extends JpaRepository<RoutingEntity, UUID> {

    List<RoutingEntity> findByProductIdOrderByProductionStageId(UUID productId);

    Optional<RoutingEntity> findByProductIdAndProductionStageId(
            UUID productId,
            UUID productionStageId);
}
