package com.atlas_erp.reporting_service.Client;

import com.atlas_erp.reporting_service.Dto.External.DepartmentDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "organization-service")
public interface OrganizationClient {

    // Gọi API số 11 của Organization Service
    @GetMapping("/departments/{id}")
    DepartmentDetailResponse getDepartmentDetail(@PathVariable("id") String id);
}