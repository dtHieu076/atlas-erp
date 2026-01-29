package com.atlas_erp.workflow_service.DTO.Request;

import lombok.Data;

@Data
public class ApproveWorkflowInstanceRequest {

    private String approverId;
    private String comment;
}
