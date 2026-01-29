package com.atlas_erp.production_service.DTO.Response;

import lombok.Data;
import java.util.Date;

@Data
public class ResumeExecutionResponse {

    private Boolean success;
    private String executionId;
    private String status;
    private Date resumedAt;
}
