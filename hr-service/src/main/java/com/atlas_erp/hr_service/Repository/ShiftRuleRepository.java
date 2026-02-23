package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.ShiftRuleEntity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRuleRepository extends JpaRepository<ShiftRuleEntity, UUID> {
    Optional<ShiftRuleEntity> findByShiftId(UUID shiftId);
}