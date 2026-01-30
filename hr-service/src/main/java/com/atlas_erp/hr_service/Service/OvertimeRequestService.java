package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Request.OvertimeRequest.CreateOvertimeRequest;
import com.atlas_erp.hr_service.Dto.Response.OvertimeRequest.CreateOvertimeResponse;

public interface OvertimeRequestService {

    CreateOvertimeResponse createOvertimeRequest(CreateOvertimeRequest request);
}

