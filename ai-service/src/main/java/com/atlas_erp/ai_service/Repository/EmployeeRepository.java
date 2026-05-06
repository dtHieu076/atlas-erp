package com.atlas_erp.ai_service.Repository;

import com.atlas_erp.ai_service.Entity.EmployeeEntity;
import com.atlas_erp.ai_service.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    List<EmployeeEntity> findByStatus(EmploymentStatus status);

    List<EmployeeEntity> findByFullNameContainingIgnoreCase(String name);

    // Tìm nhân viên mới gia nhập (Giải quyết lỗi undefined)
    List<EmployeeEntity> findByHireDateAfter(LocalDate date);

    @Query("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.status = :status")
    long countByStatus(@Param("status") EmploymentStatus status);

    @Query("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.status IN (com.atlas_erp.ai_service.enums.EmploymentStatus.ACTIVE, com.atlas_erp.ai_service.enums.EmploymentStatus.PROBATION)")
    long countActiveEmployees();

    // Thống kê giới tính (Dùng để thay thế câu SQL thủ công nếu muốn)
    @Query("SELECT e.gender as gender, COUNT(e) as count FROM EmployeeEntity e GROUP BY e.gender")
    List<Map<String, Object>> countByGender();
}