package com.atlas_erp.iam_service.Controller;

import com.atlas_erp.iam_service.Dto.Request.Permission.PermissionSearchRequest;
import com.atlas_erp.iam_service.Dto.Response.Permission.PermissionListResponse;
import com.atlas_erp.iam_service.Service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public PermissionListResponse search(PermissionSearchRequest request) {
        return permissionService.search(request);
    }
}

