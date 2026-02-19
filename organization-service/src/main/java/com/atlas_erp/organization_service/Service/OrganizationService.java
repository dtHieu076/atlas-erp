package com.atlas_erp.organization_service.Service;

import com.atlas_erp.organization_service.Dto.Request.Organization.*;
import com.atlas_erp.organization_service.Dto.Response.Organization.*;

import java.util.UUID;

public interface OrganizationService {

    OrganizationListResponse getOrganizations(OrganizationListRequest request);

    CreateOrganizationResponse createOrganization(CreateOrganizationRequest request);

    UpdateOrganizationResponse updateOrganization(UUID id, UpdateOrganizationRequest request);

    OrganizationDetailResponse getOrganizationDetail(UUID id);

    OrganizationTreeResponse getOrganizationTree();
}
