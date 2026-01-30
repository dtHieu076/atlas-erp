package com.atlas_erp.organization_service.Controller;

import com.atlas_erp.organization_service.Dto.Request.Organization.OrganizationHistoryRequest;
import com.atlas_erp.organization_service.Dto.Response.Organization.OrganizationHistoryResponse;
import com.atlas_erp.organization_service.Service.OrganizationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organization-history")
@RequiredArgsConstructor
public class OrganizationHistoryController {

    private final OrganizationHistoryService organizationHistoryService;

    // 4️⃣ GET /organization-history
    @GetMapping
    public OrganizationHistoryResponse searchHistory(OrganizationHistoryRequest request) {
        return organizationHistoryService.searchHistory(request);
    }
}
