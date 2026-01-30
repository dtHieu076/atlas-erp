package com.atlas_erp.organization_service.Controller;


import com.atlas_erp.organization_service.Dto.Request.Department.*;
import com.atlas_erp.organization_service.Dto.Response.Department.*;
import com.atlas_erp.organization_service.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    // 6️⃣ GET /departments
    @GetMapping
    public DepartmentListResponse getDepartments(DepartmentListRequest request) {
        return departmentService.getDepartments(request);
    }

    // 7️⃣ POST /departments
    @PostMapping
    public CreateDepartmentResponse createDepartment(@RequestBody CreateDepartmentRequest request) {
        return departmentService.createDepartment(request);
    }

    // 2️⃣ GET /departments/{id}
    @GetMapping("/{id}")
    public DepartmentDetailResponse getDepartmentDetail(@PathVariable UUID id) {
        return departmentService.getDepartmentDetail(id);
    }
}
