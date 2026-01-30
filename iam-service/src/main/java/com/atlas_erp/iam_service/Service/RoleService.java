package com.atlas_erp.iam_service.Service;

import com.atlas_erp.iam_service.Dto.Request.Role.AssignPermissionsRequest;
import com.atlas_erp.iam_service.Dto.Request.Role.CreateRoleRequest;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.Role.RoleListResponse;

import java.util.UUID;

public interface RoleService {

    RoleListResponse getAllRoles();

    UUID createRole(CreateRoleRequest request);

    BaseSuccessResponse assignPermissions(UUID roleId, AssignPermissionsRequest request);
}
