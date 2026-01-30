package com.atlas_erp.iam_service.Controller;

import com.atlas_erp.iam_service.Dto.Response.Permission.MyPermissionResponse;
import com.atlas_erp.iam_service.Service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final PermissionService permissionService;

    @GetMapping("/permissions")
    public MyPermissionResponse myPermissions(
            @RequestHeader("X-User-Id") UUID userId) {
        return permissionService.getMyPermissions(userId);
    }
}

