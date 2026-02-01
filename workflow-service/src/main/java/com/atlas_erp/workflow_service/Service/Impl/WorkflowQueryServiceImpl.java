package com.atlas_erp.workflow_service.Service.Impl;

import org.springframework.stereotype.Service;
import com.atlas_erp.workflow_service.Service.WorkflowQueryService;
import com.atlas_erp.workflow_service.Dto.Request.WorkflowQuery.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.*;
import java.util.Collections;

@Service
public class WorkflowQueryServiceImpl implements WorkflowQueryService {

    @Override
    public WorkflowInstanceListResponse getWorkflowInstances(
            WorkflowInstanceListRequest request) {

        WorkflowInstanceListResponse response = new WorkflowInstanceListResponse();
        response.setInstances(Collections.emptyList());
        return response;
    }

    @Override
    public AssignedWorkflowTaskResponse getAssignedTasksForCurrentUser() {

        AssignedWorkflowTaskResponse response = new AssignedWorkflowTaskResponse();
        response.setTasks(Collections.emptyList());
        return response;
    }
}
