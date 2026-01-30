package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Dto.Request.Permission.PermissionSearchRequest;
import com.atlas_erp.iam_service.Dto.Response.Permission.MyPermissionResponse;
import com.atlas_erp.iam_service.Dto.Response.Permission.PermissionListResponse;
import com.atlas_erp.iam_service.Service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Override
    public PermissionListResponse search(PermissionSearchRequest request) {
        PermissionListResponse response = new PermissionListResponse();
        response.setPermissions(List.of());
        return response;
    }

    @Override
    public MyPermissionResponse getMyPermissions(UUID userId) {
        MyPermissionResponse response = new MyPermissionResponse();

        response.setRoles(List.of());
        response.setPermissions(List.of());
        response.setDataScopes(List.of());

        return response;
    }

}
