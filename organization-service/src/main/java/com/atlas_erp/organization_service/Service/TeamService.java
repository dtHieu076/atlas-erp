package com.atlas_erp.organization_service.Service;

import com.atlas_erp.organization_service.Dto.Request.Team.*;
import com.atlas_erp.organization_service.Dto.Response.Team.*;

import java.util.UUID;

public interface TeamService {

    TeamListResponse getTeams(TeamListRequest request);

    CreateTeamResponse createTeam(CreateTeamRequest request);

    TeamDetailResponse getTeamDetail(UUID id);
}
