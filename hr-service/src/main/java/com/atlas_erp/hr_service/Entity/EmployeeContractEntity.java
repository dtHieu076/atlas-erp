package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.atlas_erp.hr_service.enums.*;

@Entity
@Table(name = "employee_contract")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "contract_template_id")
    private UUID contractTemplateId;

    @Column(name = "contract_template_name")
    private String contractTemplateName;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "base_salary", precision = 19, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "signed_date")
    private LocalDate signedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatus status;
}