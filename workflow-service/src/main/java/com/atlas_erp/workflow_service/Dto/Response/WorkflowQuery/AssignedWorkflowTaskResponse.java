package com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
