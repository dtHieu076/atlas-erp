package com.atlas_erp.organization_service.Dto.Response.Team;

import lombok.Data;
import java.util.List;

import com.atlas_erp.organization_service.Dto.TeamItemDTO;

@Data
public class TeamListResponse {

    private List<TeamItemDTO> list;
    private Long total;
}
