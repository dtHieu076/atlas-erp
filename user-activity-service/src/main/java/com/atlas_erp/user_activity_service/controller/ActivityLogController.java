package com.atlas_erp.user_activity_service.controller;

import com.atlas_erp.user_activity_service.dto.ActivityLogRequest;
import com.atlas_erp.user_activity_service.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class ActivityLogController {

    private final TrackingService trackingService;

    public ActivityLogController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/logs")
    public ResponseEntity<String> logActivity(@RequestBody ActivityLogRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            trackingService.trackActivity(request, httpRequest);
            return ResponseEntity.ok("Log sent to Kafka successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process log: " + e.getMessage());
        }
    }
    // Comment: REST endpoint POST /api/v1/logs nhận DTO, gọi Service (non-blocking)
}
