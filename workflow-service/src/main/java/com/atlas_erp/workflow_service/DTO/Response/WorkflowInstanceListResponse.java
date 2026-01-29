package com.atlas_erp.workflow_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class WorkflowInstanceListResponse {

    private List<WorkflowInstanceSummary> instances;

    @Data
    public static class WorkflowInstanceSummary {
        private String instanceId;
        private String workflowId;
        private String businessType;
        private String businessId;
        private String status;
        private Date startedAt;
        private Date completedAt;
    }
}
