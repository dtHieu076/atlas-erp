package com.atlas_erp.reporting_service.Service;

import com.atlas_erp.reporting_service.Dto.Request.ExecutiveDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.ExecutiveDashboardResponse;

public interface ExecutiveDashboardService {

    ExecutiveDashboardResponse getExecutiveDashboard(ExecutiveDashboardRequest request);
}
