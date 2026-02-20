package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_contract")
@Data
public class EmployeeContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_template_id", nullable = false)
    private ContractTemplateEntity contractTemplate;

    @Column(nullable = false)
    private String contractNo;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal baseSalary;
    private LocalDate signedDate;

    private String status; // ACTIVE, EXPIRED, TERMINATED
}