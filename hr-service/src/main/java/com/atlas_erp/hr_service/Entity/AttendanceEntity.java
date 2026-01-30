package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "attendance")
@Data
public class AttendanceEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID employeeId;

    private LocalDate workDate;
    private String shiftCode;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private BigDecimal workedHours;
    private BigDecimal overtimeHours;

    private String source;
    private Boolean locked;
}
