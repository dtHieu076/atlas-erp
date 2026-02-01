package com.atlas_erp.reporting_service.Service;

import com.atlas_erp.reporting_service.Dto.Request.KPIListRequest;
import com.atlas_erp.reporting_service.Dto.Response.KPIListResponse;

public interface KPIService {

    KPIListResponse getKPIs(KPIListRequest request);
}
