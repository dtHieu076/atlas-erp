package org.atlas_erp.dto;

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

    // Nhận từ Frontend qua POST /api/v1/logs
}