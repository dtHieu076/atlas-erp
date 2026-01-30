package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Request.LeaveRequest.CreateLeaveRequest;
import com.atlas_erp.hr_service.Dto.Response.LeaveRequest.CreateLeaveResponse;
import com.atlas_erp.hr_service.Service.LeaveRequestService;
import org.springframework.stereotype.Service;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Override
    public CreateLeaveResponse createLeaveRequest(CreateLeaveRequest request) {

        CreateLeaveResponse response = new CreateLeaveResponse();
        response.setWorkflowId("workflow-id"); // placeholder

        return response;
    }

}
