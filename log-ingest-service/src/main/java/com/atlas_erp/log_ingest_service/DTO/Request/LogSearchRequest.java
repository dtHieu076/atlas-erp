package com.atlas_erp.log_ingest_service.Dto.Request;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class LogSearchRequest {

    private String serviceName;
    private String level;
    private String traceId;
    private OffsetDateTime fromTime;
    private OffsetDateTime toTime;
    private String keyword;
    private Integer page;
    private Integer pageSize;
}
