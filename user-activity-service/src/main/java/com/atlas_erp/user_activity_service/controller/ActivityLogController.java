package com.atlas_erp.user_activity_service.controller;

import com.atlas_erp.user_activity_service.document.UserActivity;
import com.atlas_erp.user_activity_service.dto.ActivityLogQueryRequest;
import com.atlas_erp.user_activity_service.dto.ActivityLogRequest;
import com.atlas_erp.user_activity_service.dto.ApiResponse;
import com.atlas_erp.user_activity_service.service.LogQueryService;
import com.atlas_erp.user_activity_service.service.TrackingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ActivityLogController {

    private final TrackingService trackingService;
    private final LogQueryService logQueryService;

    public ActivityLogController(TrackingService trackingService, LogQueryService logQueryService) {
        this.trackingService = trackingService;
        this.logQueryService = logQueryService;
    }

    @PostMapping("/logs")
    public ResponseEntity<String> logActivity(@RequestBody ActivityLogRequest request,
            HttpServletRequest httpRequest) {
        try {
            trackingService.trackActivity(request, httpRequest);
            return ResponseEntity.ok("Log sent to Kafka successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process log: " + e.getMessage());
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Page<UserActivity>>> getActivityLogs(
            ActivityLogQueryRequest queryRequest, // Spring tự động map query params vào DTO này
            @PageableDefault(size = 20, sort = "clientTimestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserActivity> logs = logQueryService.getActivityLogs(queryRequest, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}