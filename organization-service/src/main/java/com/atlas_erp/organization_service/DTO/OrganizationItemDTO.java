package com.atlas_erp.organization_service.Dto;

import lombok.Data;
import java.util.UUID;

@Data
public class OrganizationItemDTO {

    private UUID id;
    private String code;
    private String name;
    private String taxCode;
    private String address;
    private String status;
}
