package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "leave_request")
@Data
public class LeaveRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID employeeId;

    private String leaveType; // Ví dụ: AL, SL, UL

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

    private String status; // PENDING, APPROVED, REJECTED

    private String workflowId;

    private Instant createdAt;
}