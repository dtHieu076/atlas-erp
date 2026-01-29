package com.atlas_erp.log_ingest_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.time.OffsetDateTime;

@Data
public class LogSearchResponse {

    private List<LogEntryDto> list;
    private Long total;

    @Data
    public static class LogEntryDto {
        private OffsetDateTime timestamp;
        private String serviceName;
        private String level;
        private String traceId;
        private String message;
    }
}
