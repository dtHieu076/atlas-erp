package com.atlas_erp.organization_service.Dto.Request.Organization;

import lombok.Data;
import java.time.LocalDate;

// API: POST /organizations
@Data
public class CreateOrganizationRequest {

    private String code;
    private String name;
    private String taxCode;
    private String address;
    private String legalRepresentative;

    private LocalDate validFrom;
    private LocalDate validTo;

    private String status; // ACTIVE | INACTIVE
}
