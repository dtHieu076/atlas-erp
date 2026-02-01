package com.atlas_erp.workflow_service.Dto.Response.WorkflowInstance;

import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class WorkflowHistoryResponse {

    private List<WorkflowHistoryDto> list;

    @Data
    public static class WorkflowHistoryDto {
        private Integer stepOrder;
        private String action; // APPROVE | REJECT
        private String actorId;
        private String comment;
        private Date actionAt;
    }
}
