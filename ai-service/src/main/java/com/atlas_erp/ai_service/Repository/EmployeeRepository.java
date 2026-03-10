package com.atlas_erp.ai_service.Repository;

import com.atlas_erp.ai_service.Entity.EmployeeEntity;
import com.atlas_erp.ai_service.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    List<EmployeeEntity> findByStatus(EmploymentStatus status);

    List<EmployeeEntity> findByFullNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.status = :status")
    long countByStatus(EmploymentStatus status);

    @Query("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.status IN ('ACTIVE', 'PROBATION')")
    long countActiveEmployees();
}
