package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Dto.Request.KPIListRequest;
import com.atlas_erp.reporting_service.Dto.Response.KPIListResponse;
import com.atlas_erp.reporting_service.Service.KPIService;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class KPIServiceImpl implements KPIService {

    @Override // Chưa cần gấp
    public KPIListResponse getKPIs(KPIListRequest request) {

        KPIListResponse response = new KPIListResponse();
        response.setList(Collections.emptyList());
        return response;
    }
}
