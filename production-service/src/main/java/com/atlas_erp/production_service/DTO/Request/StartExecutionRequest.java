package com.atlas_erp.production_service.DTO.Request;

import lombok.Data;
import java.util.Date;

@Data
public class StartExecutionRequest {

    private String orderId;
    private String teamId;
    private String startedBy;
    private Date startTime;
}
