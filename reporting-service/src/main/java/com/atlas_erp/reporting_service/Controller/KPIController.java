package com.atlas_erp.reporting_service.Controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.reporting_service.Dto.Request.KPIListRequest;
import com.atlas_erp.reporting_service.Dto.Response.KPIListResponse;
import com.atlas_erp.reporting_service.Service.KPIService;

@RestController
@RequestMapping("/kpis")
@RequiredArgsConstructor
public class KPIController {

    private final KPIService kpiService;

    @GetMapping
    public KPIListResponse getKPIs(KPIListRequest request) {
        return kpiService.getKPIs(request);
    }
}
