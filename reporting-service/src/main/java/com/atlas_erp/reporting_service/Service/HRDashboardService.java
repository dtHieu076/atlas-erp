package com.atlas_erp.reporting_service.Service;

import com.atlas_erp.reporting_service.Dto.Request.HRDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.HRDashboardResponse;

public interface HRDashboardService {

    HRDashboardResponse getHRDashboard(HRDashboardRequest request);
}
