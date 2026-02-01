package com.atlas_erp.log_ingest_service.Service;

import com.atlas_erp.log_ingest_service.Dto.Request.LogSearchRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.LogSearchResponse;

public interface LogQueryService {

    LogSearchResponse searchApplicationLogs(LogSearchRequest request);
}
