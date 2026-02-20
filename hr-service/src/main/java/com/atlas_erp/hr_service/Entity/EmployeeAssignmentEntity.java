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

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "position")
    private String position;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;
}
