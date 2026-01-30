package com.atlas_erp.organization_service.Service;

import com.atlas_erp.organization_service.Dto.Request.Organization.OrganizationHistoryRequest;
import com.atlas_erp.organization_service.Dto.Response.Organization.OrganizationHistoryResponse;

public interface OrganizationHistoryService {

    OrganizationHistoryResponse searchHistory(OrganizationHistoryRequest request);
}
