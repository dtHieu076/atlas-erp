package com.atlas_erp.iam_service.Dto.Response;

import lombok.Data;
import java.util.List;

import com.atlas_erp.iam_service.Dto.AuditLogDto;

@Data
public class UserAuditLogResponse {
    private String userId;
    private List<AuditLogDto> auditLogs;
}
