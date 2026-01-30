package com.atlas_erp.hr_service.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceItemDTO {

    private String employeeId;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private BigDecimal workingHours;
    private String status;
}
