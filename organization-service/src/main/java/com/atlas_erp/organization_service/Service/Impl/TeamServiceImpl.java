package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.Request.Team.*;
import com.atlas_erp.organization_service.Dto.Response.Team.*;
// import com.atlas_erp.organization_service.Repository.TeamRepository;
import com.atlas_erp.organization_service.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    // private final TeamRepository teamRepository;

    @Override
    public TeamListResponse getTeams(TeamListRequest request) {
        TeamListResponse response = new TeamListResponse();
        response.setList(List.of());
        response.setTotal(0L);
        return response;
    }

    @Override
    public CreateTeamResponse createTeam(CreateTeamRequest request) {
        CreateTeamResponse response = new CreateTeamResponse();
        response.setId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public TeamDetailResponse getTeamDetail(UUID id) {

        TeamDetailResponse response = new TeamDetailResponse();

        response.setTeamId(id.toString()); // UUID → String
        response.setTeamCode(null); // map từ entity sau
        response.setTeamName(null);
        response.setDepartmentId(null);
        response.setLeaderId(null);
        response.setStatus("ACTIVE");

        response.setCreatedAt(
                LocalDateTime.ofInstant(
                        Instant.now(),
                        ZoneId.systemDefault()));

        return response;
    }

}
