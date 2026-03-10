package com.atlas_erp.ai_service.Repository;

import com.atlas_erp.ai_service.Entity.AttendanceEntity;
import com.atlas_erp.ai_service.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {

    List<AttendanceEntity> findByEmployeeId(UUID employeeId);

    List<AttendanceEntity> findByWorkDate(LocalDate workDate);

    List<AttendanceEntity> findByEmployeeIdAndWorkDate(UUID employeeId, LocalDate workDate);

    List<AttendanceEntity> findByStatus(AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.workDate = :date AND a.status = :status")
    long countByDateAndStatus(LocalDate date, AttendanceStatus status);

    @Query("SELECT a FROM AttendanceEntity a WHERE a.workDate BETWEEN :startDate AND :endDate")
    List<AttendanceEntity> findByDateRange(LocalDate startDate, LocalDate endDate);
}
