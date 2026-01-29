package com.atlas_erp.workflow_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class AssignedWorkflowTaskResponse {

    private List<WorkflowTaskDto> tasks;

    @Data
    public static class WorkflowTaskDto {
        private String taskId;
        private String instanceId;
        private String stepName;
        private Date assignedAt;
        private Date dueDate;
    }
}
