package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository
                extends JpaRepository<EmployeeEntity, UUID>,
                JpaSpecificationExecutor<EmployeeEntity> {
        boolean existsByEmployeeCode(String employeeCode);

        Optional<EmployeeEntity> findByEmployeeCode(String employeeCode);// Đếm tổng nhân viên đang làm việc (ACTIVE)
                                                                         // tại
                                                                         // một phòng ban ở thời điểm hiện tại

        @Query("SELECT COUNT(e) FROM EmployeeEntity e " +
                        "JOIN EmployeeAssignmentEntity a ON e.id = a.employeeId " +
                        "WHERE e.status = 'ACTIVE' " +
                        "AND (:deptId IS NULL OR a.departmentId = :deptId) " +
                        "AND (a.toDate IS NULL OR a.toDate >= CURRENT_DATE)")
        long countActiveEmployees(@Param("deptId") UUID deptId);

        // Đếm nhân viên mới trong tháng
        @Query("SELECT COUNT(e) FROM EmployeeEntity e " +
                        "JOIN EmployeeAssignmentEntity a ON e.id = a.employeeId " +
                        "WHERE e.hireDate BETWEEN :start AND :end " +
                        "AND (:deptId IS NULL OR a.departmentId = :deptId)")
        long countNewHires(@Param("start") LocalDate start,
                        @Param("end") LocalDate end,
                        @Param("deptId") UUID deptId);

        @Query("""
                            SELECT DISTINCT e
                            FROM EmployeeEntity e
                            LEFT JOIN FETCH e.assignments a
                            WHERE e.id IN :ids
                        """)
        List<EmployeeEntity> findAllWithAssignmentsByIdIn(
                        @Param("ids") List<UUID> ids);

}
