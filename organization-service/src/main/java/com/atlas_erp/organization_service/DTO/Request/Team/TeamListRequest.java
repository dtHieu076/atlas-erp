package com.atlas_erp.organization_service.Dto.Request.Team;

import lombok.Data;

// API: GET /teams
@Data
public class TeamListRequest {

    private String departmentId;
    private Integer page;
    private Integer pageSize;
}
