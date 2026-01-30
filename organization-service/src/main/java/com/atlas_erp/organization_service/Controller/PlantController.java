package com.atlas_erp.organization_service.Controller;

import com.atlas_erp.organization_service.Dto.Request.Plant.CreatePlantRequest;
import com.atlas_erp.organization_service.Dto.Request.Plant.PlantListRequest;
import com.atlas_erp.organization_service.Dto.Response.Plant.CreatePlantResponse;
import com.atlas_erp.organization_service.Dto.Response.Plant.PlantListResponse;
import com.atlas_erp.organization_service.Service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
public class PlantController {

    private final PlantService plantService;

    // 4️⃣ GET /plants
    @GetMapping
    public PlantListResponse getPlants(PlantListRequest request) {
        return plantService.getPlants(request);
    }

    // 5️⃣ POST /plants
    @PostMapping
    public CreatePlantResponse createPlant(@RequestBody CreatePlantRequest request) {
        return plantService.createPlant(request);
    }
}
