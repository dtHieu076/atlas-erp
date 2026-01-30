package com.atlas_erp.accounting_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "accounting_period")
@Data
public class AccountingPeriodEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String periodCode;
    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean locked;
}
