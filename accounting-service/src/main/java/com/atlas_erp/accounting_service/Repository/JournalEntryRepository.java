package com.atlas_erp.accounting_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.atlas_erp.accounting_service.Entity.JournalEntryEntity;

public interface JournalEntryRepository extends JpaRepository<JournalEntryEntity, UUID> {
}
