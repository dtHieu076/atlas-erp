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
}
