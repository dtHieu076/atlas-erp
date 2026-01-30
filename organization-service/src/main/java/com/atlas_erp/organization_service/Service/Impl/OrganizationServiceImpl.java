package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.Request.Organization.*;
import com.atlas_erp.organization_service.Dto.Response.Organization.*;
// import com.atlas_erp.organization_service.Repository.OrganizationRepository;
import com.atlas_erp.organization_service.Service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    // private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationListResponse getOrganizations(OrganizationListRequest request) {
        OrganizationListResponse response = new OrganizationListResponse();
        response.setList(List.of());
        response.setTotal(0L);
        return response;
    }

    @Override
    public CreateOrganizationResponse createOrganization(CreateOrganizationRequest request) {
        CreateOrganizationResponse response = new CreateOrganizationResponse();
        response.setId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public UpdateOrganizationResponse updateOrganization(UUID id, UpdateOrganizationRequest request) {
        UpdateOrganizationResponse response = new UpdateOrganizationResponse();
        response.setSuccess(true);
        return response;
    }

    @Override
    public OrganizationDetailResponse getOrganizationDetail(UUID id) {

        OrganizationDetailResponse response = new OrganizationDetailResponse();

        response.setOrganizationId(id.toString()); // UUID → String
        response.setOrganizationCode(null); // sẽ map từ entity
        response.setOrganizationName(null);
        response.setTaxCode(null);
        response.setAddress(null);
        response.setStatus("ACTIVE");
        response.setEstablishedDate(null);

        // Instant → LocalDateTime
        response.setCreatedAt(
                LocalDateTime.ofInstant(
                        Instant.now(),
                        ZoneId.systemDefault()));

        return response;
    }

}
