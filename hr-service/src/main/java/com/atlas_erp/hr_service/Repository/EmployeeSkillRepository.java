package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.EmployeeSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeSkillRepository
        extends JpaRepository<EmployeeSkillEntity, UUID> {

    List<EmployeeSkillEntity> findByEmployeeId(UUID employeeId);

    List<EmployeeSkillEntity> findBySkillCode(String skillCode);

    List<EmployeeSkillEntity> findByCertified(Boolean certified);
}
