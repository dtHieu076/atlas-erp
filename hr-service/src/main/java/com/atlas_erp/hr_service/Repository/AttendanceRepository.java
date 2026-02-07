package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepository
        extends JpaRepository<AttendanceEntity, UUID>,
        JpaSpecificationExecutor<AttendanceEntity> {

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

    @Modifying
    @Query("UPDATE AttendanceEntity a SET a.locked = true WHERE a.workDate BETWEEN :start AND :end")
    int lockAttendanceRecords(LocalDate start, LocalDate end);

    @Query("""
                SELECT COUNT(a) FROM AttendanceEntity a
                WHERE a.workDate BETWEEN :start AND :end
                AND (:empIds IS NULL OR a.employeeId IN :empIds)
            """)
    Integer countWorkingDays(LocalDate start, LocalDate end, List<UUID> empIds);

    @Query("""
                SELECT SUM(a.overtimeHours) FROM AttendanceEntity a
                WHERE a.workDate BETWEEN :start AND :end
                AND (:empIds IS NULL OR a.employeeId IN :empIds)
            """)
    Double sumOvertimeHoursInRange(LocalDate start, LocalDate end, List<UUID> empIds);

    // Tính tổng giờ tăng ca trong kỳ
    @Query("SELECT SUM(att.overtimeHours) FROM AttendanceEntity att " +
            "JOIN EmployeeAssignmentEntity a ON att.employeeId = a.employeeId " +
            "WHERE att.workDate BETWEEN :start AND :end " +
            "AND (:deptId IS NULL OR a.departmentId = :deptId)")
    BigDecimal sumOvertimeHours(@Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("deptId") UUID deptId);

    // Đếm tổng số lượt đi làm (để tính Attendance Rate)
    @Query("SELECT COUNT(att) FROM AttendanceEntity att " +
            "JOIN EmployeeAssignmentEntity a ON att.employeeId = a.employeeId " +
            "WHERE att.workDate BETWEEN :start AND :end " +
            "AND att.checkIn IS NOT NULL " +
            "AND (:deptId IS NULL OR a.departmentId = :deptId)")
    long countActualWorkDays(@Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("deptId") UUID deptId);
}
