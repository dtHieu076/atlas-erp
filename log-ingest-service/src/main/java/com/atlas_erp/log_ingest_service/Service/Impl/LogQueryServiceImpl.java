package com.atlas_erp.log_ingest_service.Service.Impl;

import com.atlas_erp.log_ingest_service.Dto.Request.LogSearchRequest;
import com.atlas_erp.log_ingest_service.Dto.Response.LogSearchResponse;
import com.atlas_erp.log_ingest_service.Service.LogQueryService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LogQueryServiceImpl implements LogQueryService {

    @Override
    public LogSearchResponse searchApplicationLogs(
            LogSearchRequest request) {

        LogSearchResponse response = new LogSearchResponse();
        response.setList(Collections.emptyList());
        response.setTotal(0L);

        return response;
    }
}
