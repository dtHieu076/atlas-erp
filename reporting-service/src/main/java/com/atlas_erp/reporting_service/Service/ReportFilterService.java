package com.atlas_erp.reporting_service.Service;

import com.atlas_erp.reporting_service.Dto.Response.ReportFilterResponse;

public interface ReportFilterService {

    ReportFilterResponse getFiltersByReportType(String reportType);
}
