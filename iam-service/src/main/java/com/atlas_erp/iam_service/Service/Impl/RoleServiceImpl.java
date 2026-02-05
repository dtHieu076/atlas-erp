package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Dto.Request.Role.AssignPermissionsRequest;
import com.atlas_erp.iam_service.Dto.Request.Role.CreateRoleRequest;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.Role.RoleListResponse;
import com.atlas_erp.iam_service.Entity.PermissionEntity;
import com.atlas_erp.iam_service.Entity.RoleEntity;
import com.atlas_erp.iam_service.Entity.RolePermissionEntity;
import com.atlas_erp.iam_service.Event.dtoEvent.RoleCreatedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.RolePermissionUpdatedEvent;
import com.atlas_erp.iam_service.Event.serviceEvent.RoleEventService;
import com.atlas_erp.iam_service.Repository.RoleRepository;
import com.atlas_erp.iam_service.Repository.PermissionRepository;
import com.atlas_erp.iam_service.Repository.RolePermissionRepository;
import com.atlas_erp.iam_service.Service.RoleService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleEventService roleEventService;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleListResponse getAllRoles() {
        List<RoleEntity> roleEntities = roleRepository.findAll();

        RoleListResponse response = new RoleListResponse();
        response.setRoles(roleEntities);
        return response;
    }

    @Override
    public UUID createRole(CreateRoleRequest request) {

        // 1. Validate input
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("Role code is required");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }

        // 2. Validate unique code
        if (roleRepository.existsByRoleCode(request.getCode())) {
            throw new IllegalStateException(
                    "Role code already exists: " + request.getCode());
        }

        // 3. Create RoleEntity
        RoleEntity role = new RoleEntity();
        role.setRoleCode(request.getCode());
        role.setRoleName(request.getName());
        role.setDescription(request.getDescription());

        // 4. Save
        RoleEntity savedRole = roleRepository.save(role);

        // 5. Publish event
        roleEventService.publishRoleCreated(
                new RoleCreatedEvent(
                        savedRole.getId(),
                        savedRole.getRoleCode(),
                        savedRole.getRoleName(),
                        Instant.now()));

        // 6. Return roleId
        return savedRole.getId();
    }

    @Override
    @Transactional
    public BaseSuccessResponse assignPermissions(
            UUID roleId,
            AssignPermissionsRequest request) {

        // 1️⃣ Validate role exists
        roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        // 2️⃣ Validate permissions
        List<UUID> permissionIds = request.getPermissionIds()
                .stream()
                .map(UUID::fromString)
                .toList();

        List<PermissionEntity> permissions = permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new IllegalArgumentException(
                    "One or more permissions not found");
        }

        // 3️⃣ Replace mappings (atomic)
        rolePermissionRepository.deleteByRoleId(roleId);

        List<RolePermissionEntity> mappings = permissionIds.stream()
                .map(permissionId -> {
                    RolePermissionEntity rp = new RolePermissionEntity();
                    rp.setRoleId(roleId);
                    rp.setPermissionId(permissionId);
                    return rp;
                })
                .toList();

        rolePermissionRepository.saveAll(mappings);

        // 4️⃣ Publish event
        roleEventService.publishRolePermissionUpdated(
                new RolePermissionUpdatedEvent(
                        roleId,
                        permissionIds,
                        Instant.now()));

        // 5️⃣ Return success
        return BaseSuccessResponse.success();
    }

}
