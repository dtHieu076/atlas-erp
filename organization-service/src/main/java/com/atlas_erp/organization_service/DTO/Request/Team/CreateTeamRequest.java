package com.atlas_erp.organization_service.Dto.Request.Team;

import lombok.Data;

// API: POST /teams
@Data
public class CreateTeamRequest {

    private String code;
    private String name;
    private String departmentId;
    private String leaderId;
    private String status;
}
