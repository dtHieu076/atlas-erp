package com.atlas_erp.production_service.Controller;

import com.atlas_erp.production_service.Dto.Request.Defect.CreateDefectRequest;
import com.atlas_erp.production_service.Dto.Response.Defect.CreateDefectResponse;
import com.atlas_erp.production_service.Service.DefectService;
import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;

    // 7️⃣ POST /defects
    @PostMapping
    public CreateDefectResponse logDefect(@RequestBody CreateDefectRequest request) {
        return defectService.logDefect(request);
    }
}
