package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AttendanceLockedEvent {
    private Integer month;
    private Integer year;
    private LocalDate lockDate;
    private String status;
}