package com.atlas_erp.workflow_service.Service;

import com.atlas_erp.workflow_service.Dto.Request.Workflow.*;
import com.atlas_erp.workflow_service.Dto.Response.Workflow.*;

public interface WorkflowService {

    CreateWorkflowResponse createWorkflow(CreateWorkflowRequest request);

    WorkflowListResponse getWorkflows(WorkflowListRequest request);
}
