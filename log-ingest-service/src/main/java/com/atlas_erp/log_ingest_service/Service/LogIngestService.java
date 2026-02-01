package com.atlas_erp.log_ingest_service.Service;

import com.atlas_erp.log_ingest_service.Dto.Request.CreateLogRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.CreateLogResponse;

public interface LogIngestService {

    CreateLogResponse ingestLog(CreateLogRequest request);
}
