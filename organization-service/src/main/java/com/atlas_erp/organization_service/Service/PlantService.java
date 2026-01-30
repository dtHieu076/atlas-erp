package com.atlas_erp.organization_service.Service;

import com.atlas_erp.organization_service.Dto.Request.Plant.*;
import com.atlas_erp.organization_service.Dto.Response.Plant.*;

public interface PlantService {

    PlantListResponse getPlants(PlantListRequest request);

    CreatePlantResponse createPlant(CreatePlantRequest request);
}
