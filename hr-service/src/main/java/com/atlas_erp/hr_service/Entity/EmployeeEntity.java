package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "employee")
@Data
public class EmployeeEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "permanent_address")
    private String permanentAddress;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "dependents_count")
    private Integer dependentsCount;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<EmployeeAssignmentEntity> assignments;
}
