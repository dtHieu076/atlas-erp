package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.OvertimeRequest.CreateOvertimeRequest;
import com.atlas_erp.hr_service.Dto.Response.OvertimeRequest.CreateOvertimeResponse;
import com.atlas_erp.hr_service.Service.OvertimeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/overtime-requests")
@RequiredArgsConstructor
public class OvertimeRequestController {

    private final OvertimeRequestService overtimeRequestService;

    // 🔟 POST /overtime-requests
    @PostMapping
    public CreateOvertimeResponse createOvertimeRequest(
            @RequestBody CreateOvertimeRequest request) {
        return overtimeRequestService.createOvertimeRequest(request);
    }
}
