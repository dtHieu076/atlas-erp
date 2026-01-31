package com.atlas_erp.production_service.Service;

import com.atlas_erp.production_service.Dto.Request.Defect.CreateDefectRequest;
import com.atlas_erp.production_service.Dto.Response.Defect.CreateDefectResponse;

public interface DefectService {

    CreateDefectResponse logDefect(CreateDefectRequest request);
}
