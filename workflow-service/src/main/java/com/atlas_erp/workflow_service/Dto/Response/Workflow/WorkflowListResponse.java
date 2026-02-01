package com.atlas_erp.workflow_service.Dto.Response.Workflow;

import lombok.Data;
import java.util.List;

@Data
public class WorkflowListResponse {

    private List<WorkflowSummary> workflows;

    @Data
    public static class WorkflowSummary {
        private String workflowId;
        private String workflowCode;
        private String workflowName;
        private String type;
        private String status;
        private Integer version;
    }
}
