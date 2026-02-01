package com.atlas_erp.workflow_service.Service;

import com.atlas_erp.workflow_service.Dto.Request.WorkflowInstance.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowInstance.*;

public interface WorkflowInstanceService {

        CreateWorkflowInstanceResponse createWorkflowInstance(CreateWorkflowInstanceRequest request);

        ApproveWorkflowInstanceResponse approveInstance(String instanceId, ApproveWorkflowInstanceRequest request);

        RejectWorkflowInstanceResponse rejectInstance(String instanceId, RejectWorkflowInstanceRequest request);

        WorkflowHistoryResponse getApprovalHistory(String instanceId);
}
