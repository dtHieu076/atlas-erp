package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.Request.Organization.OrganizationHistoryRequest;
import com.atlas_erp.organization_service.Dto.Response.Organization.OrganizationHistoryResponse;
import com.atlas_erp.organization_service.Service.OrganizationHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationHistoryServiceImpl implements OrganizationHistoryService {

    @Override
    public OrganizationHistoryResponse searchHistory(OrganizationHistoryRequest request) {
        OrganizationHistoryResponse response = new OrganizationHistoryResponse();
        response.setHistoryList(List.of());
        return response;
    }
}
