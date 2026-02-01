package com.atlas_erp.log_ingest_service.Dto.Request;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class AuditLogSearchRequest {

    private String userId;
    private String action;
    private String resourceType;
    private OffsetDateTime fromTime;
    private OffsetDateTime toTime;
    private Integer page;
    private Integer pageSize;
}
