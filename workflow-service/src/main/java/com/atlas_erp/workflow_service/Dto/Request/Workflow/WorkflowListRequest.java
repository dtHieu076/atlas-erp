package com.atlas_erp.workflow_service.Dto.Request.Workflow;

import lombok.Data;

@Data
public class WorkflowListRequest {

    private String type;
    private String status;
}
