package com.atlas_erp.organization_service.DTO.Response;

import com.atlas_erp.organization_service.DTO.TeamItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class TeamListResponse {

    private List<TeamItemDTO> list;
    private Long total;
}
