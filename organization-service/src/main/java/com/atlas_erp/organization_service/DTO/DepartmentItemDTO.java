package com.atlas_erp.organization_service.Dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DepartmentItemDTO {

    private UUID id;
    private String code;
    private String name;
    private UUID plantId;
    private String status;
}
