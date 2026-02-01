package com.atlas_erp.accounting_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.atlas_erp.accounting_service.Entity.AccountingPeriodEntity;

public interface AccountingPeriodRepository extends JpaRepository<AccountingPeriodEntity, UUID> {
}
