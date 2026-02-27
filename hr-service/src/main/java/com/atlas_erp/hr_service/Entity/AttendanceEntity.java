package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.math.BigDecimal;
import com.atlas_erp.hr_service.enums.AttendanceStatus;
import com.atlas_erp.hr_service.enums.LeaveType;

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

    private UUID shiftId; // reference ShiftEntity

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    // Backwards-compatible fields (existing code may still use hours)
    private BigDecimal workedHours;
    private BigDecimal overtimeHours;

    // ===== SNAPSHOT CALCULATED =====
    private Integer workMinutes; // tổng phút làm việc
    private Integer otMinutes; // tổng phút OT
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status; // WORKING, LEAVE, ABSENT...

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType; // PAID / UNPAID

    private String note;

    private Boolean locked;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        if (locked == null)
            locked = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
