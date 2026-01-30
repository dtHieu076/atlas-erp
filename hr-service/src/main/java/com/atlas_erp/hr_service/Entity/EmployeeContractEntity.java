package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "employee_contract")
@Data
public class EmployeeContractEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID employeeId;

    private String contractType;
    private String contractNo;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal baseSalary;
    private LocalDate signedDate;

    private String status;
}
