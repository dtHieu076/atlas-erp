package com.atlas_erp.workflow_service.DTO.Request;

import lombok.Data;
import java.util.Date;

@Data
public class WorkflowInstanceListRequest {

    private String status;
    private Date fromDate;
    private Date toDate;
}
