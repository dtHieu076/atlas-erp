package com.atlas_erp.payroll_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "payroll")
@Data
public class PayrollEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID employeeId;
    private UUID payrollPeriodId;

    private BigDecimal grossSalary;
    private BigDecimal netSalary;

    private String status;
}
