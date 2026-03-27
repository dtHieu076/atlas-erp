package com.atlas_erp.user_activity_service.dto;

import lombok.Data;

@Data
public class ActivityLogQueryRequest {
    private String userId;
    private String sessionId;
    private String action;
    private Long startDate; // clientTimestamp ms
    private Long endDate; // clientTimestamp ms

    private int page = 0;
    private int size = 20;
    private String sort = "clientTimestamp,desc"; // field,direction

    // Helper to create Pageable (for controller/service)
    // Can be used internally
}
