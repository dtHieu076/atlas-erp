package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;
import com.atlas_erp.hr_service.enums.*;

@Entity
@Table(name = "employee")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderType gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "personal_email")
    private String personalEmail;

    @Column(name = "work_email")
    private String workEmail;

    @Column(name = "permanent_address", columnDefinition = "TEXT")
    private String permanentAddress;

    @Column(name = "current_address", columnDefinition = "TEXT")
    private String currentAddress;

    @Column(name = "hometown")
    private String hometown;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "dependents_count")
    private Integer dependentsCount;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "employment_type")
    private String employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmploymentStatus status;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "manager_id")
    private UUID managerId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        if (status == null)
            status = EmploymentStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}