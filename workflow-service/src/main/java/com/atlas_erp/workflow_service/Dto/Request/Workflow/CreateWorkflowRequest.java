package com.atlas_erp.workflow_service.Dto.Request.Workflow;

import lombok.Data;
import java.util.List;

@Data
public class CreateWorkflowRequest {

    private String code;
    private String name;
    private String businessType;
    private String description;
    private List<WorkflowStepRequest> steps;

    @Data
    public static class WorkflowStepRequest {
        private Integer stepOrder;
        private String roleCode;
        private String actionType; // APPROVE | REVIEW
    }
}
