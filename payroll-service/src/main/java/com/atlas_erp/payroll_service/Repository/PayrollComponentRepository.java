package com.atlas_erp.payroll_service.Repository;

import com.atlas_erp.payroll_service.Entity.PayrollComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface PayrollComponentRepository
        extends JpaRepository<PayrollComponentEntity, UUID> {

    List<PayrollComponentEntity> findByPayrollId(UUID payrollId);
}
