package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.OvertimeRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequestEntity, UUID> {
}