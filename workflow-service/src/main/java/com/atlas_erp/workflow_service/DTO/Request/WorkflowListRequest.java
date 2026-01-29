package com.atlas_erp.workflow_service.DTO.Request;

import lombok.Data;

@Data
public class WorkflowListRequest {

    private String type;
    private String status;
}
