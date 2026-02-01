package com.atlas_erp.log_ingest_service.Controller;

import com.atlas_erp.log_ingest_service.Dto.Request.LogSearchRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.LogSearchResponse;
import com.atlas_erp.log_ingest_service.Service.LogQueryService;
import org.springframework.web.bind.annotation.*;
import lombok.*;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogSearchController {

    private final LogQueryService logQueryService;

    @GetMapping("/search")
    public LogSearchResponse searchLogs(LogSearchRequest request) {
        return logQueryService.searchApplicationLogs(request);
    }
}
