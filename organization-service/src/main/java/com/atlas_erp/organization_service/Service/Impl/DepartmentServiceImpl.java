package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.Request.Department.*;
import com.atlas_erp.organization_service.Dto.Response.Department.*;
// import com.atlas_erp.organization_service.Repository.DepartmentRepository;
import com.atlas_erp.organization_service.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    // private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentListResponse getDepartments(DepartmentListRequest request) {
        DepartmentListResponse response = new DepartmentListResponse();
        response.setList(List.of());
        response.setTotal(0L);
        return response;
    }

    @Override
    public CreateDepartmentResponse createDepartment(CreateDepartmentRequest request) {
        CreateDepartmentResponse response = new CreateDepartmentResponse();
        response.setId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public DepartmentDetailResponse getDepartmentDetail(UUID id) {

        DepartmentDetailResponse response = new DepartmentDetailResponse();

        response.setDepartmentId(id.toString()); // UUID → String
        response.setDepartmentCode(null); // sẽ map từ entity sau
        response.setDepartmentName(null);
        response.setOrganizationId(null);
        response.setManagerId(null);
        response.setStatus("ACTIVE");

        // Instant → LocalDateTime (theo UTC, hoặc ZoneId.systemDefault() tuỳ chuẩn hệ
        // thống)
        response.setCreatedAt(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        return response;
    }

}
