package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "employee_skill")
@Data
public class EmployeeSkillEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID employeeId;

    private String skillCode;
    private String skillName;
    private String skillLevel;

    private Boolean certified;
    private LocalDate certifiedDate;
}
