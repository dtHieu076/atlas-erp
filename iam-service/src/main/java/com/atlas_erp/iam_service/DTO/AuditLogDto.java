package com.atlas_erp.iam_service.Dto;

import lombok.Data;
import java.time.Instant;

@Data
public class AuditLogDto {
    private String action;
    private String description;
    private String performedBy;
    private Instant performedAt;
    private String ipAddress;
}
