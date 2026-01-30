package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Request.OvertimeRequest.CreateOvertimeRequest;
import com.atlas_erp.hr_service.Dto.Response.OvertimeRequest.CreateOvertimeResponse;
import com.atlas_erp.hr_service.Service.OvertimeRequestService;
import org.springframework.stereotype.Service;

@Service
public class OvertimeRequestServiceImpl implements OvertimeRequestService {

    @Override
    public CreateOvertimeResponse createOvertimeRequest(CreateOvertimeRequest request) {

        CreateOvertimeResponse response = new CreateOvertimeResponse();
        response.setWorkflowId("workflow-id"); // placeholder

        return response;
    }

}
