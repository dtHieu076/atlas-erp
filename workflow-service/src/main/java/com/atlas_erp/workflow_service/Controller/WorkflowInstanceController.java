package com.atlas_erp.workflow_service.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.atlas_erp.workflow_service.Service.WorkflowInstanceService;
import com.atlas_erp.workflow_service.Dto.Request.WorkflowInstance.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowInstance.*;

@RestController
@RequestMapping("/instances")
@RequiredArgsConstructor
public class WorkflowInstanceController {

    private final WorkflowInstanceService workflowInstanceService;

    @PostMapping
    public CreateWorkflowInstanceResponse createInstance(
            @RequestBody CreateWorkflowInstanceRequest request) {
        return workflowInstanceService.createWorkflowInstance(request);
    }

    @PostMapping("/{id}/approve")
    public ApproveWorkflowInstanceResponse approve(
            @PathVariable String id,
            @RequestBody ApproveWorkflowInstanceRequest request) {
        return workflowInstanceService.approveInstance(id, request);
    }

    @PostMapping("/{id}/reject")
    public RejectWorkflowInstanceResponse reject(
            @PathVariable String id,
            @RequestBody RejectWorkflowInstanceRequest request) {
        return workflowInstanceService.rejectInstance(id, request);
    }

    @GetMapping("/{id}/history")
    public WorkflowHistoryResponse history(@PathVariable String id) {
        return workflowInstanceService.getApprovalHistory(id);
    }
}
