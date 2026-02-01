package com.atlas_erp.log_ingest_service.Controller;

import com.atlas_erp.log_ingest_service.Dto.Request.CreateLogRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.CreateLogResponse;
import com.atlas_erp.log_ingest_service.Service.LogIngestService;
import org.springframework.web.bind.annotation.*;
import lombok.*;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogIngestController {

    private final LogIngestService logIngestService;

    @PostMapping
    public CreateLogResponse ingestLog(
            @RequestBody CreateLogRequest request) {
        return logIngestService.ingestLog(request);
    }
}
