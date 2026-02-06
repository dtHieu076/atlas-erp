package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository
        extends JpaRepository<EmployeeEntity, UUID>,
        JpaSpecificationExecutor<EmployeeEntity> {
    boolean existsByEmployeeCode(String employeeCode);

    Optional<EmployeeEntity> findByEmployeeCode(String employeeCode);
}
