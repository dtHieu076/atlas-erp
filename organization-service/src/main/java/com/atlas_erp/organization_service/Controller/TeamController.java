package com.atlas_erp.organization_service.Controller;

import com.atlas_erp.organization_service.Dto.Request.Team.*;
import com.atlas_erp.organization_service.Dto.Response.Team.*;
import com.atlas_erp.organization_service.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // 8️⃣ GET /teams
    @GetMapping
    public TeamListResponse getTeams(TeamListRequest request) {
        return teamService.getTeams(request);
    }

    // 9️⃣ POST /teams
    @PostMapping
    public CreateTeamResponse createTeam(@RequestBody CreateTeamRequest request) {
        return teamService.createTeam(request);
    }

    // 3️⃣ GET /teams/{id}
    @GetMapping("/{id}")
    public TeamDetailResponse getTeamDetail(@PathVariable UUID id) {
        return teamService.getTeamDetail(id);
    }
}
