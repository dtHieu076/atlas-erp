package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "overtime_request")
@Data
public class OvertimeRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID employeeId;
    private LocalDate otDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED
    private String workflowId;
    
    private Instant createdAt;
}