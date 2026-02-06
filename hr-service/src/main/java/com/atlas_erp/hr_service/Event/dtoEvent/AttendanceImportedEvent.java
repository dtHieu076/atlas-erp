package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AttendanceImportedEvent {
    private Instant importTime;
    private Integer totalRecords;
    private List<UUID> records; // Danh sách ID nhân viên vừa được nạp công
}
