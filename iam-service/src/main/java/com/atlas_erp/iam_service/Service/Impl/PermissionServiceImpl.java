package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Cache.serviceCache.PermissionCacheService;
import com.atlas_erp.iam_service.Dto.DataScopeDto;
import com.atlas_erp.iam_service.Dto.PermissionDto;
import com.atlas_erp.iam_service.Dto.Request.Permission.PermissionSearchRequest;
import com.atlas_erp.iam_service.Dto.Response.Permission.MyPermissionResponse;
import com.atlas_erp.iam_service.Dto.Response.Permission.PermissionListResponse;
import com.atlas_erp.iam_service.Entity.PermissionEntity;
import com.atlas_erp.iam_service.Entity.RoleEntity;
import com.atlas_erp.iam_service.Entity.RolePermissionEntity;
import com.atlas_erp.iam_service.Entity.UserDataScopeEntity;
import com.atlas_erp.iam_service.Entity.UserRoleEntity;
import com.atlas_erp.iam_service.Repository.PermissionRepository;
import com.atlas_erp.iam_service.Repository.RolePermissionRepository;
import com.atlas_erp.iam_service.Repository.RoleRepository;
import com.atlas_erp.iam_service.Repository.UserDataScopeRepository;
import com.atlas_erp.iam_service.Repository.UserRoleRepository;
import com.atlas_erp.iam_service.Service.PermissionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserDataScopeRepository userDataScopeRepository;
    private final PermissionCacheService permissionCacheService;

    @Override
    public PermissionListResponse search(PermissionSearchRequest request) {

        List<PermissionEntity> entities;

        if (request.getModule() == null || request.getModule().isBlank()) {
            entities = permissionRepository.findAll();
        } else {
            entities = permissionRepository.findByModule(request.getModule());
        }

        List<PermissionDto> permissions = entities.stream()
                .map(this::toDto)
                .toList();

        PermissionListResponse response = new PermissionListResponse();
        response.setPermissions(permissions);
        return response;
    }

    @Override
    public MyPermissionResponse getMyPermissions(UUID userId) {

        // 1️⃣ Try cache
        MyPermissionResponse cached = permissionCacheService.getMyPermission(userId);

        if (cached != null) {
            return cached;
        }

        // 2️⃣ Load roles của user
        List<UserRoleEntity> userRoles = userRoleRepository.findByUserId(userId);

        List<UUID> roleIds = userRoles.stream()
                .map(UserRoleEntity::getRoleId)
                .toList();

        if (roleIds.isEmpty()) {
            MyPermissionResponse empty = MyPermissionResponse.empty();
            permissionCacheService.putMyPermission(userId, empty);
            return empty;
        }

        // 3️⃣ Load role codes
        List<RoleEntity> roles = roleRepository.findAllById(roleIds);

        List<String> roleCodes = roles.stream()
                .map(RoleEntity::getRoleCode)
                .toList();

        // 4️⃣ Resolve permissions
        List<RolePermissionEntity> rolePermissions = rolePermissionRepository.findByRoleIdIn(roleIds);

        List<UUID> permissionIds = rolePermissions.stream()
                .map(RolePermissionEntity::getPermissionId)
                .distinct()
                .toList();

        List<PermissionEntity> permissions = permissionRepository.findAllById(permissionIds);

        List<String> permissionCodes = permissions.stream()
                .map(p -> String.join(
                        ":",
                        p.getModule(),
                        p.getFunction(),
                        p.getAction()))
                .distinct()
                .toList();

        // 5️⃣ Load data scopes
        List<UserDataScopeEntity> scopes = userDataScopeRepository.findByUserId(userId);

        List<DataScopeDto> dataScopes = scopes.stream()
                .map(s -> {
                    DataScopeDto dto = new DataScopeDto();
                    dto.setType(s.getScopeType().name());
                    dto.setScopeId(
                            s.getScopeId() != null
                                    ? s.getScopeId().toString()
                                    : null);
                    return dto;
                })
                .toList();

        // 6️⃣ Build response
        MyPermissionResponse response = new MyPermissionResponse();
        response.setRoles(roleCodes);
        response.setPermissions(permissionCodes);
        response.setDataScopes(dataScopes);

        // 7️⃣ Save cache
        permissionCacheService.putMyPermission(userId, response);

        return response;
    }

    private PermissionDto toDto(PermissionEntity entity) {
        return PermissionDto.builder()
                .id(entity.getId().toString())
                .module(entity.getModule())
                .function(entity.getFunction())
                .action(entity.getAction())
                .build();
    }

}
