package com.atlas_erp.iam_service.DTO.Response;

import com.atlas_erp.iam_service.DTO.AuditLogDto;
import lombok.Data;
import java.util.List;

@Data
public class UserAuditLogResponse {
    private String userId;
    private List<AuditLogDto> auditLogs;
}
