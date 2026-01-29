package com.atlas_erp.organization_service.DTO.Response;

import lombok.Data;
import java.time.LocalDateTime;

// API: GET /teams/{id}
@Data
public class TeamDetailResponse {

    private String teamId;
    private String teamCode;
    private String teamName;
    private String departmentId;
    private String leaderId;
    private String status;
    private LocalDateTime createdAt;
}
