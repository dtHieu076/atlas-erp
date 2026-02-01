package com.atlas_erp.reporting_service.Controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.reporting_service.Dto.Request.ReportExportRequest;
import com.atlas_erp.reporting_service.Dto.Response.ReportExportResponse;
import com.atlas_erp.reporting_service.Service.ReportExportService;
import com.atlas_erp.reporting_service.Dto.Response.ReportFilterResponse;
import com.atlas_erp.reporting_service.Service.ReportFilterService;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportExportService reportExportService;
    private final ReportFilterService reportFilterService;

    @GetMapping("/export")
    public ReportExportResponse exportReport(ReportExportRequest request) {
        return reportExportService.exportReport(request);
    }

    @GetMapping("/{type}/filters")
    public ReportFilterResponse getReportFilters(
            @PathVariable("type") String reportType) {
        return reportFilterService.getFiltersByReportType(reportType);
    }
}
