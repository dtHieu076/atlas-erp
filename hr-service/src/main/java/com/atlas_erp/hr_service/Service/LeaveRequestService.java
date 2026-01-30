package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Request.LeaveRequest.CreateLeaveRequest;
import com.atlas_erp.hr_service.Dto.Response.LeaveRequest.CreateLeaveResponse;

public interface LeaveRequestService {

    CreateLeaveResponse createLeaveRequest(CreateLeaveRequest request);
}
