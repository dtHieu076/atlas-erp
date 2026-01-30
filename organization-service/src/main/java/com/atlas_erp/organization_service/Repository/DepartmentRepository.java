package com.atlas_erp.organization_service.Repository;

import com.atlas_erp.organization_service.Entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, UUID> {
}
