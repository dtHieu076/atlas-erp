package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Dto.Request.HRDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.HRDashboardResponse;
import com.atlas_erp.reporting_service.Service.HRDashboardService;
import org.springframework.stereotype.Service;

@Service
public class HRDashboardServiceImpl
        implements HRDashboardService {

    @Override
    public HRDashboardResponse getHRDashboard(
            HRDashboardRequest request) {

        return new HRDashboardResponse();
    }
}
