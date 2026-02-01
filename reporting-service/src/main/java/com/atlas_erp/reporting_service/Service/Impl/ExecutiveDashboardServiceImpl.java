package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Dto.Request.ExecutiveDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.ExecutiveDashboardResponse;
import com.atlas_erp.reporting_service.Service.ExecutiveDashboardService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.math.BigDecimal;

@Service
public class ExecutiveDashboardServiceImpl
        implements ExecutiveDashboardService {

    @Override
    public ExecutiveDashboardResponse getExecutiveDashboard(
            ExecutiveDashboardRequest request) {

        ExecutiveDashboardResponse response = new ExecutiveDashboardResponse();

        response.setRevenue(BigDecimal.ZERO);
        response.setExpense(BigDecimal.ZERO);
        response.setProfit(BigDecimal.ZERO);
        response.setTotalEmployee(0);
        response.setProductivityIndex(BigDecimal.ZERO);
        response.setSnapshotDate(new Date());

        return response;
    }
}
