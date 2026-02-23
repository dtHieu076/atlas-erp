package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.ShiftEntity;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<ShiftEntity, UUID> {

}
