package com.atlas_erp.log_ingest_service.Service;

import com.atlas_erp.log_ingest_service.Dto.Request.AuditLogSearchRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.AuditLogSearchResponse;

public interface AuditLogService {

    AuditLogSearchResponse searchAuditLogs(AuditLogSearchRequest request);
}
