package com.atlas_erp.iam_service.Service;

import com.atlas_erp.iam_service.Dto.Request.User.AssignRolesRequest;
import com.atlas_erp.iam_service.Dto.Request.User.CreateUserRequest;
import com.atlas_erp.iam_service.Dto.Request.User.ForcePasswordResetRequest;
import com.atlas_erp.iam_service.Dto.Request.User.LockUserRequest;
import com.atlas_erp.iam_service.Dto.Request.User.UnlockUserRequest;
import com.atlas_erp.iam_service.Dto.Request.User.UpdateUserStatusRequest;
import com.atlas_erp.iam_service.Dto.Request.User.UserSearchRequest;
import com.atlas_erp.iam_service.Dto.Response.User.ForcePasswordResetResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UnlockUserResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UserDataScopeResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UserDetailResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UserListResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UserRolesResponse;
import java.util.UUID;

public interface UserService {

    UserListResponse searchUsers(UserSearchRequest request);

    UUID createUser(CreateUserRequest request);

    void updateStatus(UUID userId, UpdateUserStatusRequest request);

    void lockUser(UUID userId, LockUserRequest request);

    UnlockUserResponse unlockUser(UUID userId, UnlockUserRequest request);

    ForcePasswordResetResponse forcePasswordReset(UUID userId, ForcePasswordResetRequest request);

    void assignRoles(UUID userId, AssignRolesRequest request);

    UserDetailResponse getUserDetail(UUID userId);

    UserRolesResponse getUserRoles(UUID userId);

    UserDataScopeResponse getUserDataScopes(UUID userId);
}
