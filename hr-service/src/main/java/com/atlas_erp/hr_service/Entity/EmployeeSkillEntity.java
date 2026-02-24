package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_skill", uniqueConstraints = @UniqueConstraint(columnNames = { "employee_id", "skill_id" }))
@Data
public class EmployeeSkillEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    @Column(nullable = false)
    private Integer currentLevel;

    private Boolean certified;
    private LocalDate certifiedDate;

    private String note;
}