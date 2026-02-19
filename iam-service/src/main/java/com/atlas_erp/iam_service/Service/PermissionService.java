package com.atlas_erp.iam_service.Service;

import com.atlas_erp.iam_service.Dto.Request.Permission.PermissionSearchRequest;
import com.atlas_erp.iam_service.Dto.Response.Permission.MyPermissionResponse;
import com.atlas_erp.iam_service.Dto.Response.Permission.PermissionListResponse;
import com.atlas_erp.iam_service.Entity.PermissionEntity;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    PermissionListResponse search(PermissionSearchRequest request);

    MyPermissionResponse getMyPermissions(UUID userId);

    List<PermissionEntity> getAll();
}
