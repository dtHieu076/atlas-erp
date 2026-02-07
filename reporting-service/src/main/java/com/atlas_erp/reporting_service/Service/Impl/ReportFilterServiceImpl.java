package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Dto.Response.ReportFilterResponse;
import com.atlas_erp.reporting_service.Service.ReportFilterService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportFilterServiceImpl
        implements ReportFilterService {

    @Override // Chưa cần thiết
    public ReportFilterResponse getFiltersByReportType(
            String reportType) {

        ReportFilterResponse response = new ReportFilterResponse();
        response.setReportType(reportType);
        response.setFilters(Collections.emptyList());
        return response;
    }
}
