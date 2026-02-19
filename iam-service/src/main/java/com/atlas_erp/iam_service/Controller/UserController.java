package com.atlas_erp.iam_service.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.atlas_erp.iam_service.Dto.Request.User.*;
import com.atlas_erp.iam_service.Dto.Response.User.*;
import com.atlas_erp.iam_service.Dto.Response.ApiResponse;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Service.UserService;
import com.atlas_erp.iam_service.Dto.UserDto;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách người dùng thành công",
                        userService.getAllUsers()));
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<Void>> toggleLock(@PathVariable UUID id) {

        userService.toggleLock(id);

        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật trạng thái khóa thành công", null));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<Void>> updateRoles(
            @PathVariable UUID id,
            @RequestBody List<UUID> roleIds) {

        userService.updateUserRoles(id, roleIds);

        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật vai trò thành công", null));
    }
}
