package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Response.BatchTrace.BatchTraceResponse;
import com.atlas_erp.inventory_service.Service.BatchTraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/batches")
@RequiredArgsConstructor
public class BatchTraceController {

    private final BatchTraceService batchTraceService;

    @GetMapping("/{id}/trace")
    public BatchTraceResponse traceBatchHistory(
            @PathVariable UUID id) {
        return batchTraceService.traceBatchHistory(id);
    }
}
