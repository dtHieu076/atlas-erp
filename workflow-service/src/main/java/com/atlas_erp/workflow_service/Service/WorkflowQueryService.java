package com.atlas_erp.workflow_service.Service;

import com.atlas_erp.workflow_service.Dto.Request.WorkflowQuery.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.*;

public interface WorkflowQueryService {

    WorkflowInstanceListResponse getWorkflowInstances(WorkflowInstanceListRequest request);

    AssignedWorkflowTaskResponse getAssignedTasksForCurrentUser();
}
