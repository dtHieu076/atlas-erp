package com.atlas_erp.iam_service.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.atlas_erp.iam_service.Dto.Request.User.*;
import com.atlas_erp.iam_service.Dto.Response.User.*;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserListResponse search(UserSearchRequest request) {
        return userService.searchUsers(request);
    }

    @PostMapping
    public CreateUserResponse create(@RequestBody CreateUserRequest request) {
        return new CreateUserResponse((userService.createUser(request)).toString());
    }

    @PutMapping("/{id}")
    public BaseSuccessResponse updateStatus(@PathVariable UUID id, @RequestBody UpdateUserStatusRequest request) {
        return userService.updateStatus(id, request);
    }

    @PostMapping("/{id}/lock")
    public BaseSuccessResponse lock(@PathVariable UUID id, @RequestBody LockUserRequest request) {
        return userService.lockUser(id, request);
    }

    @PostMapping("/{id}/unlock")
    public UnlockUserResponse unlock(@PathVariable UUID id, @RequestBody UnlockUserRequest request) {
        return userService.unlockUser(id, request);
    }

    @PostMapping("/{id}/force-password-reset")
    public ForcePasswordResetResponse forceReset(@PathVariable UUID id,
            @RequestBody ForcePasswordResetRequest request) {
        return userService.forcePasswordReset(id, request);
    }

    @PostMapping("/{id}/roles")
    public BaseSuccessResponse assignRoles(@PathVariable UUID id, @RequestBody AssignRolesRequest request) {
        return userService.assignRoles(id, request);
    }

    @GetMapping("/{id}")
    public UserDetailResponse detail(@PathVariable UUID id) {
        return userService.getUserDetail(id);
    }

    @GetMapping("/{id}/roles")
    public UserRolesResponse roles(@PathVariable UUID id) {
        return userService.getUserRoles(id);
    }

    @GetMapping("/{id}/data-scopes")
    public UserDataScopeResponse dataScopes(@PathVariable UUID id) {
        return userService.getUserDataScopes(id);
    }
}
