package com.atlas_erp.payroll_service.Repository;

import com.atlas_erp.payroll_service.Entity.PayrollPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollPeriodRepository
        extends JpaRepository<PayrollPeriodEntity, UUID> {

    Optional<PayrollPeriodEntity> findByPeriodCode(String periodCode);
}
