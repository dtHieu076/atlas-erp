package com.atlas_erp.organization_service.DTO.Request;

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
