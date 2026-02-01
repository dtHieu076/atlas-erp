package com.atlas_erp.workflow_service.Dto.Request.WorkflowInstance;

import lombok.Data;

@Data
public class ApproveWorkflowInstanceRequest {

    private String approverId;
    private String comment;
}
