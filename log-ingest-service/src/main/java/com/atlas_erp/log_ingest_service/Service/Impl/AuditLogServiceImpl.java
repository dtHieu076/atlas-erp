package com.atlas_erp.log_ingest_service.Service.Impl;

import com.atlas_erp.log_ingest_service.Dto.Request.AuditLogSearchRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.AuditLogSearchResponse;
import com.atlas_erp.log_ingest_service.Service.AuditLogService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Override
    public AuditLogSearchResponse searchAuditLogs(
            AuditLogSearchRequest request) {

        AuditLogSearchResponse response = new AuditLogSearchResponse();
        response.setList(Collections.emptyList());
        response.setTotal(0L);

        return response;
    }
}
