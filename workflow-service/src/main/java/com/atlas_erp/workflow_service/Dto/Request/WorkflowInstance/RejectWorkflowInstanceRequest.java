package com.atlas_erp.workflow_service.Dto.Request.WorkflowInstance;

import lombok.Data;

@Data
public class RejectWorkflowInstanceRequest {

    private String approverId;
    private String reason;
}
