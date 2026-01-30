package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.Request.Plant.*;
import com.atlas_erp.organization_service.Dto.Response.Plant.*;
// import com.atlas_erp.organization_service.Repository.PlantRepository;
import com.atlas_erp.organization_service.Service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    // private final PlantRepository plantRepository;

    @Override
    public PlantListResponse getPlants(PlantListRequest request) {
        PlantListResponse response = new PlantListResponse();
        response.setList(List.of());
        response.setTotal(0L);
        return response;
    }

    @Override
    public CreatePlantResponse createPlant(CreatePlantRequest request) {
        CreatePlantResponse response = new CreatePlantResponse();
        response.setId(UUID.randomUUID().toString());
        return response;
    }
}
