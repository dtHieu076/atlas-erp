package com.atlas_erp.accounting_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "journal_entry")
@Data
public class JournalEntryEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDate entryDate;
    private String description;

    private UUID accountingPeriodId;

    private String status;

    private Instant postedAt;
    private UUID postedBy;

    // reverse instead of delete
    private UUID reversalEntryId;
}
