package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "employee")
@Data
public class EmployeeEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String employeeCode;
    private String fullName;
    private String gender;
    private LocalDate dob;

    private String nationalId;
    private String phone;
    private String email;

    private String permanentAddress;
    private String currentAddress;

    private String educationLevel;
    private String maritalStatus;
    private Integer dependentsCount;

    private LocalDate hireDate;
    private String employmentType;
    private String status;

    private Instant createdAt;
}
