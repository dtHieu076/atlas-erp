package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.LeaveRequestEntity; // Đảm bảo đường dẫn này đúng với project của bạn
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, UUID> {

    @Query("""
        SELECT COUNT(l) > 0 FROM LeaveRequestEntity l 
        WHERE l.employeeId = :empId 
        AND l.status IN :statuses
        AND (:start <= l.toDate AND :end >= l.fromDate)
    """)
    boolean existsByEmployeeIdAndStatusInAndDateRange(
        @Param("empId") UUID empId, 
        @Param("statuses") List<String> statuses, 
        @Param("start") LocalDate start, 
        @Param("end") LocalDate end
    );
}