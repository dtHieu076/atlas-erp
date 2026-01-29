package com.atlas_erp.workflow_service.DTO.Request;

import lombok.Data;

@Data
public class CreateWorkflowInstanceRequest {

    private String workflowId;
    private String businessType;
    private String businessId;
    private String requesterId;
}
