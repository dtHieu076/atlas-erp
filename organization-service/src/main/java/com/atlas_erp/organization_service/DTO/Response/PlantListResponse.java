package com.atlas_erp.organization_service.DTO.Response;

import com.atlas_erp.organization_service.DTO.PlantItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class PlantListResponse {

    private List<PlantItemDTO> list;
    private Long total;
}
