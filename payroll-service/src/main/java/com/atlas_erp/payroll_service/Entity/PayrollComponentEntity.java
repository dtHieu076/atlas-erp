package com.atlas_erp.payroll_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "payroll_component")
@Data
public class PayrollComponentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID payrollId;

    private String componentCode;
    private String componentType;

    private BigDecimal amount;
}
