package com.atlas_erp.payroll_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "payroll_period")
@Data
public class PayrollPeriodEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String periodCode;
    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean locked;
    private Instant lockedAt;
    private UUID lockedBy;
}
