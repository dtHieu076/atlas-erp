package com.atlas_erp.reporting_service.Service;

import com.atlas_erp.reporting_service.Dto.Request.ReportExportRequest;
import com.atlas_erp.reporting_service.Dto.Response.ReportExportResponse;

public interface ReportExportService {

    ReportExportResponse exportReport(ReportExportRequest request);
}
