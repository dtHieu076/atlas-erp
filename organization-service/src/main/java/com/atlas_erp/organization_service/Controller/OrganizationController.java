package com.atlas_erp.organization_service.Controller;

import com.atlas_erp.organization_service.Dto.Request.Organization.*;
import com.atlas_erp.organization_service.Dto.Response.Organization.*;
import com.atlas_erp.organization_service.Service.OrganizationService;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.organization_service.Dto.Response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public OrganizationListResponse getOrganizations(OrganizationListRequest request) {
        return organizationService.getOrganizations(request);
    }

    @PostMapping
    public CreateOrganizationResponse createOrganization(
            @RequestBody CreateOrganizationRequest request) {
        return organizationService.createOrganization(request);
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<OrganizationTreeResponse>> getTree() {
        return ResponseEntity.ok(
                ApiResponse.success("Fetch organization tree successfully",
                        organizationService.getOrganizationTree()));
    }

    @PutMapping("/{id}")
    public UpdateOrganizationResponse updateOrganization(
            @PathVariable UUID id,
            @RequestBody UpdateOrganizationRequest request) {
        return organizationService.updateOrganization(id, request);
    }

    @GetMapping("/{id}")
    public OrganizationDetailResponse getOrganizationDetail(@PathVariable UUID id) {
        return organizationService.getOrganizationDetail(id);
    }
}
