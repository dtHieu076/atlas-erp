package com.atlas_erp.log_ingest_service.DTO.Request;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class CreateLogRequest {

    private OffsetDateTime timestamp;
    private String serviceName;
    private String serviceInstance;
    private String level; // INFO | WARN | ERROR
    private String traceId;
    private String spanId;
    private String message;
    private Map<String, String> data;
}
