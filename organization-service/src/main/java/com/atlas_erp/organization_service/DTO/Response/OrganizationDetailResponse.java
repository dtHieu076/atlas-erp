package com.atlas_erp.organization_service.DTO.Response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

// API: GET /organizations/{id}
@Data
public class OrganizationDetailResponse {

    private String organizationId;
    private String organizationCode;
    private String organizationName;
    private String taxCode;
    private String address;
    private String status;
    private LocalDate establishedDate;
    private LocalDateTime createdAt;
}
