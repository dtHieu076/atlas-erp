package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.DefectLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DefectLogRepository extends JpaRepository<DefectLogEntity, UUID> {

    List<DefectLogEntity> findByProductionExecutionId(UUID productionExecutionId);
}
