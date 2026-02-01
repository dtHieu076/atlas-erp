package com.atlas_erp.log_ingest_service.Controller;

import com.atlas_erp.log_ingest_service.Dto.Request.AuditLogSearchRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.AuditLogSearchResponse;
import com.atlas_erp.log_ingest_service.Service.AuditLogService;
import org.springframework.web.bind.annotation.*;
import lombok.*;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public AuditLogSearchResponse searchAuditLogs(
            AuditLogSearchRequest request) {
        return auditLogService.searchAuditLogs(request);
    }
}
