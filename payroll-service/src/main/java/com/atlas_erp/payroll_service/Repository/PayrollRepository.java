package com.atlas_erp.payroll_service.Repository;

import com.atlas_erp.payroll_service.Entity.PayrollEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface PayrollRepository
                extends JpaRepository<PayrollEntity, UUID> {

        Page<PayrollEntity> findByPayrollPeriodId(UUID periodId, Pageable pageable);

        Optional<PayrollEntity> findByPayrollPeriodIdAndEmployeeId(
                        UUID periodId, UUID employeeId);

        @Modifying
        void deleteByPayrollPeriodId(UUID payrollPeriodId);

        // Dùng cho trường hợp lọc theo danh sách nhân viên của một phòng ban
        Page<PayrollEntity> findByPayrollPeriodIdAndEmployeeIdIn(UUID periodId, List<UUID> employeeIds,
                        Pageable pageable);

}
