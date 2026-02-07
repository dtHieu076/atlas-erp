package com.atlas_erp.workflow_service.Client;

import com.atlas_erp.workflow_service.Dto.External.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service")
public interface IamClient {

    // Gọi API số 12: GET /users/{id}/roles
    @GetMapping("/users/{id}/roles")
    UserRolesResponse getUserRoles(@PathVariable("id") String id);

    // Bổ sung vào IamClient.java
    @GetMapping("/roles")
    RoleListResponse getAllRoles();
}