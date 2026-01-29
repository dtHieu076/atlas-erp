package com.atlas_erp.organization_service.DTO.Request;

import lombok.Data;

// API: GET /teams
@Data
public class TeamListRequest {

    private String departmentId;
    private Integer page;
    private Integer pageSize;
}
