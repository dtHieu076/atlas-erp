package com.atlas_erp.iam_service.Controller;

import com.atlas_erp.iam_service.Dto.Request.Role.AssignPermissionsRequest;
import com.atlas_erp.iam_service.Dto.Request.Role.CreateRoleRequest;
import com.atlas_erp.iam_service.Dto.Response.Role.CreateRoleResponse;
import com.atlas_erp.iam_service.Dto.Response.Role.RoleListResponse;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public RoleListResponse list() {
        return roleService.getAllRoles();
    }

    @PostMapping
    public CreateRoleResponse create(@RequestBody CreateRoleRequest request) {
        return new CreateRoleResponse((roleService.createRole(request)).toString());
    }

    @PostMapping("/{id}/permissions")
    public BaseSuccessResponse assignPermissions(@PathVariable UUID id, @RequestBody AssignPermissionsRequest request) {
        return roleService.assignPermissions(id, request);
    }
}
