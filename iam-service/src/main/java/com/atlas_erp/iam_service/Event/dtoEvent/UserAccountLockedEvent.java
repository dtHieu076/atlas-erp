package com.atlas_erp.iam_service.Event.dtoEvent;

import lombok.Data;
import lombok.Builder;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
public class UserAccountLockedEvent {
    private UUID userId; // ID của người bị khóa
    private String email; // Email để Service Notification gửi mail báo cho user
    private String reason; // Lý do khóa để hiển thị trong Audit Log
    private UUID lockedBy; // ID của Admin thực hiện khóa (rất quan trọng cho Audit)
    private Instant lockedAt; // Thời điểm khóa
}