package com.atlas_erp.log_ingest_service.Service.Impl;

import com.atlas_erp.log_ingest_service.Dto.Request.CreateLogRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.CreateLogResponse;
import com.atlas_erp.log_ingest_service.Service.LogIngestService;
import org.springframework.stereotype.Service;

@Service
public class LogIngestServiceImpl implements LogIngestService {

    @Override // Chưa cần thiết
    public CreateLogResponse ingestLog(CreateLogRequest request) {

        CreateLogResponse response = new CreateLogResponse();
        response.setSuccess(true);

        return response;
    }
}
