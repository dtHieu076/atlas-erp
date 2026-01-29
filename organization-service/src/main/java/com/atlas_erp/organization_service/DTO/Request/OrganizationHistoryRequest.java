package com.atlas_erp.organization_service.DTO.Request;

import lombok.Data;
import java.time.LocalDate;

// API: GET /organization-history
@Data
public class OrganizationHistoryRequest {

    private String entityType; // ORGANIZATION / DEPARTMENT / TEAM
    private String entityId;
    private LocalDate fromDate;
    private LocalDate toDate;
}
