package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface EmployeeRepository
                extends JpaRepository<EmployeeEntity, UUID>,
                JpaSpecificationExecutor<EmployeeEntity> {
        boolean existsByEmployeeCode(String employeeCode);

        Optional<EmployeeEntity> findByEmployeeCode(String employeeCode);

        List<EmployeeEntity> findByStatus(com.atlas_erp.hr_service.enums.EmploymentStatus status);

        @Query("SELECT e FROM EmployeeEntity e WHERE e.status = com.atlas_erp.hr_service.enums.EmploymentStatus.ACTIVE")
        List<EmployeeEntity> findActiveEmployees();

        @Query("SELECT DISTINCT e FROM EmployeeEntity e " +
                        "JOIN EmployeeAssignmentEntity ea ON e.id = ea.employeeId " +
                        "WHERE ea.teamId IN :teamIds AND ea.toDate IS NULL AND e.status = com.atlas_erp.hr_service.enums.EmploymentStatus.ACTIVE")
        List<EmployeeEntity> findActiveByTeams(@Param("teamIds") List<UUID> teamIds);

        @Query("SELECT e FROM EmployeeEntity e WHERE e.id IN :ids AND e.status = com.atlas_erp.hr_service.enums.EmploymentStatus.ACTIVE")
        List<EmployeeEntity> findActiveByIds(@Param("ids") List<UUID> ids);

        @Query("SELECT e FROM EmployeeEntity e WHERE e.workEmail IN :emails AND e.status = com.atlas_erp.hr_service.enums.EmploymentStatus.ACTIVE")
        List<EmployeeEntity> findActiveByWorkEmails(@Param("emails") List<String> emails);
}
