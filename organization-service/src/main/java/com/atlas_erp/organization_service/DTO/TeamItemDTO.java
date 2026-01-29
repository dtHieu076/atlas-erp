package com.atlas_erp.organization_service.DTO;

import lombok.Data;

@Data
public class TeamItemDTO {

    private String id;
    private String code;
    private String name;
    private String departmentId;
    private String leaderId;
    private String status;
}
