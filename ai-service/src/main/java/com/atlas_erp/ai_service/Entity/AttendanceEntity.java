package com.atlas_erp.ai_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.math.BigDecimal;
import com.atlas_erp.ai_service.enums.AttendanceStatus;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private LocalDate workDate;

    private UUID shiftId;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private BigDecimal workedHours;
    private BigDecimal overtimeHours;

    private Integer workMinutes;
    private Integer otMinutes;
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private String leaveType;

    private String note;

    private Boolean locked;

    private Instant createdAt;
    private Instant updatedAt;
}
