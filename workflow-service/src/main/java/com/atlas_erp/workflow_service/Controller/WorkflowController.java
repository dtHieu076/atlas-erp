package com.atlas_erp.workflow_service.Controller;

import com.atlas_erp.workflow_service.Dto.Request.Workflow.*;
import com.atlas_erp.workflow_service.Dto.Response.Workflow.*;
import com.atlas_erp.workflow_service.Service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.atlas_erp.workflow_service.Dto.Response.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public CreateWorkflowResponse createWorkflow(
            @RequestBody CreateWorkflowRequest request) {
        return workflowService.createWorkflow(request);
    }

    @GetMapping
    public WorkflowListResponse listWorkflows(WorkflowListRequest request) {
        return workflowService.getWorkflows(request);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WorkflowAllListResponse>>> getAllWorkflows() {
        // Gọi service lấy danh sách
        List<WorkflowAllListResponse> response = workflowService.getAllWorkflows();

        // Đóng gói vào ApiResponse theo chuẩn mảng dữ liệu
        ApiResponse<List<WorkflowAllListResponse>> apiResponse = ApiResponse.<List<WorkflowAllListResponse>>builder()
                .success(true)
                .message("Successfully retrieved all workflows")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
