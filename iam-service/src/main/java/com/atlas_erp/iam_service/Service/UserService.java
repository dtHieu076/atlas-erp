package com.atlas_erp.iam_service.Service;

import com.atlas_erp.iam_service.Dto.Request.User.*;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.User.*;
import com.atlas_erp.iam_service.Dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserListResponse searchUsers(UserSearchRequest request);

    CreateUserResponse createUser(CreateUserRequest request);

    BaseSuccessResponse updateStatus(UUID userId, UpdateUserStatusRequest request);

    BaseSuccessResponse lockUser(UUID userId, LockUserRequest request);

    UnlockUserResponse unlockUser(UUID userId, UnlockUserRequest request);

    ForcePasswordResetResponse forcePasswordReset(UUID userId, ForcePasswordResetRequest request);

    BaseSuccessResponse assignRoles(UUID userId, AssignRolesRequest request);

    UserDetailResponse getUserDetail(UUID userId);

    UserRolesResponse getUserRoles(UUID userId);

    UserDataScopeResponse getUserDataScopes(UUID userId);

    List<UserDto> getAllUsers();

    void toggleLock(UUID userId);

    void updateUserRoles(UUID userId, List<UUID> roleIds);
}
