package com.atlas_erp.workflow_service.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.atlas_erp.workflow_service.Service.WorkflowQueryService;
import com.atlas_erp.workflow_service.Dto.Request.WorkflowQuery.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.*;

@RestController
@RequestMapping("/instances")
@RequiredArgsConstructor
public class WorkflowQueryController {

    private final WorkflowQueryService workflowQueryService;

    @GetMapping
    public WorkflowInstanceListResponse listInstances(
            WorkflowInstanceListRequest request) {
        return workflowQueryService.getWorkflowInstances(request);
    }

    @GetMapping(params = "assigned_to=me")
    public AssignedWorkflowTaskResponse assignedToMe() {
        return workflowQueryService.getAssignedTasksForCurrentUser();
    }
}
