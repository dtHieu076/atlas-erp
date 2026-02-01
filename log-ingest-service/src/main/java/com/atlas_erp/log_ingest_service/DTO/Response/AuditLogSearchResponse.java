package com.atlas_erp.log_ingest_service.Dto.Response;

import lombok.Data;
import java.util.List;
import java.time.OffsetDateTime;

@Data
public class AuditLogSearchResponse {

    private List<AuditLogDto> list;
    private Long total;

    @Data
    public static class AuditLogDto {
        private OffsetDateTime timestamp;
        private String userId;
        private String action;
        private String resourceId;
        private String result;
    }
}
