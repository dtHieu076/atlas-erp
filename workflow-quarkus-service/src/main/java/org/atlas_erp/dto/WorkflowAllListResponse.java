package org.atlas_erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowAllListResponse {

    private UUID id;
    private String code;
    private String name;
    private String businessType;
    private String description;
    private String status; // ACTIVE | INACTIVE
    private Integer version;

    // Danh sách các bước tương ứng với steps: WorkflowStep[]
    private List<WorkflowStepResponse> steps;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkflowStepResponse {
        private UUID id;
        private UUID workflowId;
        private String name;
        private Integer stepOrder;
        private UUID roleId;
        private String actionType; // APPROVE | REVIEW
        private String conditionRule;
        private Integer durationHours;
    }
}