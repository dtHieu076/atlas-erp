package com.atlas_erp.organization_service.Dto.Response.Plant;

import lombok.Data;
import java.util.List;

import com.atlas_erp.organization_service.Dto.PlantItemDTO;

@Data
public class PlantListResponse {

    private List<PlantItemDTO> list;
    private Long total;
}
