package com.atlas_erp.iam_service.Controller;

import com.atlas_erp.iam_service.Dto.Request.Role.AssignPermissionsRequest;
import com.atlas_erp.iam_service.Dto.Request.Role.CreateRoleRequest;
import com.atlas_erp.iam_service.Dto.Response.Role.CreateRoleResponse;
import com.atlas_erp.iam_service.Dto.Response.Role.RoleListResponse;
import com.atlas_erp.iam_service.Dto.Response.ApiResponse;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Service.RoleService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public CreateRoleResponse create(@RequestBody CreateRoleRequest request) {
        return new CreateRoleResponse((roleService.createRole(request)).toString());
    }

    @PostMapping("/{id}/permissions")
    public BaseSuccessResponse assignPermissions(@PathVariable UUID id, @RequestBody AssignPermissionsRequest request) {
        return roleService.assignPermissions(id, request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<RoleListResponse>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách vai trò thành công",
                        roleService.getAllRoles()));
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Void>> updatePermissions(
            @PathVariable UUID id,
            @RequestBody List<UUID> permissionIds) {

        roleService.updateRolePermissions(id, permissionIds);

        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật quyền thành công", null));
    }
}
