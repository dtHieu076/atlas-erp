package com.atlas_erp.iam_service.Service;

import com.atlas_erp.iam_service.Dto.Request.User.*;
import com.atlas_erp.iam_service.Dto.Response.User.*;
import java.util.UUID;

public interface UserService {

    UserListResponse searchUsers(UserSearchRequest request);

    CreateUserResponse createUser(CreateUserRequest request);

    void updateStatus(UUID userId, UpdateUserStatusRequest request);

    void lockUser(UUID userId, LockUserRequest request);

    UnlockUserResponse unlockUser(UUID userId, UnlockUserRequest request);

    ForcePasswordResetResponse forcePasswordReset(UUID userId, ForcePasswordResetRequest request);

    void assignRoles(UUID userId, AssignRolesRequest request);

    UserDetailResponse getUserDetail(UUID userId);

    UserRolesResponse getUserRoles(UUID userId);

    UserDataScopeResponse getUserDataScopes(UUID userId);
}
