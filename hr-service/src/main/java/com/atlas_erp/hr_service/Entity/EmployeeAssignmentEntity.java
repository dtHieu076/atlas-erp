package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_assignment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "job_title_id")
    private UUID jobTitleId;

    @Column(name = "job_title_name")
    private String jobTitleName;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;
}