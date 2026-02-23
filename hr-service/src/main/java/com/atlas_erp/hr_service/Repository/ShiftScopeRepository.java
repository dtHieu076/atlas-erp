package com.atlas_erp.hr_service.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.atlas_erp.hr_service.Entity.ShiftScopeEntity;

public interface ShiftScopeRepository extends JpaRepository<ShiftScopeEntity, UUID> {
    List<ShiftScopeEntity> findByShiftId(UUID shiftId);
}