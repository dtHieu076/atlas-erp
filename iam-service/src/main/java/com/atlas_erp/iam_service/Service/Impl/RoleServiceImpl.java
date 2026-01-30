package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Dto.Request.Role.AssignPermissionsRequest;
import com.atlas_erp.iam_service.Dto.Request.Role.CreateRoleRequest;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.Role.RoleListResponse;
import com.atlas_erp.iam_service.Service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Override
    public RoleListResponse getAllRoles() {
        RoleListResponse response = new RoleListResponse();
        response.setRoles(List.of());
        return response;
    }

    @Override
    public UUID createRole(CreateRoleRequest request) {
        return UUID.randomUUID();
    }

    @Override
    public BaseSuccessResponse assignPermissions(UUID roleId, AssignPermissionsRequest request) {
        return BaseSuccessResponse.success();
    }
}
