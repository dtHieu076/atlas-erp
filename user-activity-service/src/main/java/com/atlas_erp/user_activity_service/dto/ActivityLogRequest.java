package com.atlas_erp.user_activity_service.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ActivityLogRequest {
    private String sessionId;
    private String userId;
    private String action;
    private String page;
    private Long clientTimestamp; // milliseconds
    private Map<String, Object> metadata;
    private String userName;

    // Comment: DTO nhận từ Frontend qua POST /api/v1/logs
}
