package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.LeaveRequest.CreateLeaveRequest;
import com.atlas_erp.hr_service.Dto.Response.LeaveRequest.CreateLeaveResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.atlas_erp.hr_service.Service.LeaveRequestService;

@RestController
@RequestMapping("/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    // 9️⃣ POST /leave-requests
    @PostMapping
    public CreateLeaveResponse createLeaveRequest(
            @RequestBody CreateLeaveRequest request) {
        return leaveRequestService.createLeaveRequest(request);
    }
}
