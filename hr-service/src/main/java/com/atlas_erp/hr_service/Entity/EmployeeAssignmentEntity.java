package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "employee_assignment")
@Data
public class EmployeeAssignmentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID employeeId;

    // reference Organization service
    private UUID departmentId;
    private UUID teamId;

    private String position;
    private String gradeLevel;

    private LocalDate fromDate;
    private LocalDate toDate;
}
