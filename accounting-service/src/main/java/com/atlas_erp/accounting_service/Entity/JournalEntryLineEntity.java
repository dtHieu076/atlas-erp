package com.atlas_erp.accounting_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "journal_entry_line")
@Data
public class JournalEntryLineEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID journalEntryId;
    private UUID accountId;

    private BigDecimal debit;
    private BigDecimal credit;

    private String costCenterCode;

    private String referenceType;
    private UUID referenceId;
}
