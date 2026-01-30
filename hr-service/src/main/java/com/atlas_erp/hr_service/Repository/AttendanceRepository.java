package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepository
        extends JpaRepository<AttendanceEntity, UUID> {

    List<AttendanceEntity> findByEmployeeIdAndWorkDateBetween(
            UUID employeeId,
            LocalDate fromDate,
            LocalDate toDate);

    List<AttendanceEntity> findByWorkDateBetween(
            LocalDate fromDate,
            LocalDate toDate);

    @Query("""
                select sum(a.overtimeHours)
                from AttendanceEntity a
                where a.workDate between :from and :to
            """)
    Double sumOvertimeHours(LocalDate from, LocalDate to);

    boolean existsByWorkDateAndLockedTrue(LocalDate workDate);
}
