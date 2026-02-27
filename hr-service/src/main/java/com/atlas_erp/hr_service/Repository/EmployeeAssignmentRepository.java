package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeAssignmentRepository
                extends JpaRepository<EmployeeAssignmentEntity, UUID> {

        List<EmployeeAssignmentEntity> findByEmployeeIdOrderByFromDateDesc(
                        UUID employeeId);

        Optional<EmployeeAssignmentEntity> findFirstByEmployeeIdAndToDateIsNull(
                        UUID employeeId);

        List<EmployeeAssignmentEntity> findByDepartmentIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                        UUID departmentId,
                        LocalDate from,
                        LocalDate to);

        List<EmployeeAssignmentEntity> findByEmployeeId(UUID employeeId);

        Optional<EmployeeAssignmentEntity> findTopByEmployeeIdAndToDateIsNullOrderByFromDateDesc(UUID employeeId);

        List<EmployeeAssignmentEntity> findByEmployeeIdInAndToDateIsNull(List<UUID> employeeIds);

        // Load all assignments for a batch of employees (used for fallback/latest
        // lookup)
        List<EmployeeAssignmentEntity> findByEmployeeIdIn(List<UUID> employeeIds);
}
