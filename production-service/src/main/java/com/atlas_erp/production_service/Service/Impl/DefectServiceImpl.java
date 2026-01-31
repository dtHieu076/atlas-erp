package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Dto.Request.Defect.CreateDefectRequest;
import com.atlas_erp.production_service.Dto.Response.Defect.CreateDefectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.atlas_erp.production_service.Service.DefectService;
// import com.atlas_erp.production_service.Entity.DefectLogEntity;
// import com.atlas_erp.production_service.Repository.DefectLogRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefectServiceImpl implements DefectService {

    // private final DefectLogRepository defectLogRepository;

    @Override
    public CreateDefectResponse logDefect(CreateDefectRequest request) {

        CreateDefectResponse response = new CreateDefectResponse();

        // sinh id giả để tượng trưng
        response.setId(UUID.randomUUID().toString());

        return response;
    }

}
