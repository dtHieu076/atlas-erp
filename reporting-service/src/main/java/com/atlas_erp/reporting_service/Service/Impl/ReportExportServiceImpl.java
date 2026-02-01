package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Dto.Request.ReportExportRequest;
import com.atlas_erp.reporting_service.Dto.Response.ReportExportResponse;
import com.atlas_erp.reporting_service.Service.ReportExportService;
import org.springframework.stereotype.Service;

@Service
public class ReportExportServiceImpl
        implements ReportExportService {

    @Override
    public ReportExportResponse exportReport(
            ReportExportRequest request) {

        ReportExportResponse response = new ReportExportResponse();
        response.setFileName("report." + request.getFormat());
        response.setFile(new byte[0]);

        return response;
    }
}
