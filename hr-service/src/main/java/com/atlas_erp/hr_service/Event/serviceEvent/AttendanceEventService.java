package com.atlas_erp.hr_service.Event.serviceEvent;

import com.atlas_erp.hr_service.Event.dtoEvent.AttendanceAdjustmentRequestedEvent;
import com.atlas_erp.hr_service.Event.dtoEvent.AttendanceImportedEvent;
import com.atlas_erp.hr_service.Event.dtoEvent.AttendanceLockedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Thông báo khi dữ liệu chấm công được import thành công.
     * Payroll Service sẽ lắng nghe để tính toán lại lương dự kiến.
     */
    public void publishAttendanceImportedEvent(AttendanceImportedEvent event) {
        log.info("Publishing AttendanceImportedEvent: {} records", event.getTotalRecords());
        try {
            kafkaTemplate.send("hr.attendance.imported", event);
        } catch (Exception e) {
            log.error("Failed to publish attendance event: {}", e.getMessage());
        }
    }

    public void publishAttendanceLockedEvent(AttendanceLockedEvent event) {
        kafkaTemplate.send("hr.attendance.locked",
                String.format("%d-%d", event.getYear(), event.getMonth()),
                event);
    }

    public void publishAdjustmentRequestEvent(AttendanceAdjustmentRequestedEvent event) {
        // Gửi vào topic chung cho các quy trình phê duyệt
        kafkaTemplate.send("workflow.attendance.adjustment.requested", event);
    }
}
