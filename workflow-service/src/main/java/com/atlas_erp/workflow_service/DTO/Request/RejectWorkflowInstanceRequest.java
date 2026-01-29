package com.atlas_erp.workflow_service.DTO.Request;

import lombok.Data;

@Data
public class RejectWorkflowInstanceRequest {

    private String approverId;
    private String reason;
}
