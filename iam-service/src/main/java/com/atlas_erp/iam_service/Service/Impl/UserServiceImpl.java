package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Dto.Request.User.*;
import com.atlas_erp.iam_service.Dto.Response.User.*;
import com.atlas_erp.iam_service.Entity.AppUserEntity;
import com.atlas_erp.iam_service.Entity.UserIdentityEntity;
import com.atlas_erp.iam_service.Event.dtoEvent.UserCreatedEvent;
import com.atlas_erp.iam_service.Event.serviceKafka.UserEventProducer;
import com.atlas_erp.iam_service.Repository.AppUserRepository;
import com.atlas_erp.iam_service.Repository.UserIdentityRepository;
import com.atlas_erp.iam_service.Service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserIdentityRepository userIdentityRepository;
    private AppUserRepository appUserRepository;
    private UserEventProducer userEventProducer;

    @Override
    public UserListResponse searchUsers(UserSearchRequest request) {
        UserListResponse response = new UserListResponse();
        response.setUsers(List.of());
        response.setTotal(0L);
        return response;
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        // 1. Validate input
        if (request.getGoogleUid() == null || request.getGoogleUid().isBlank()) {
            throw new IllegalArgumentException("googleUid is required");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        // 2. Check identity already exists
        userIdentityRepository
                .findByProviderAndProviderUid("GOOGLE", request.getGoogleUid())
                .ifPresent(i -> {
                    throw new IllegalStateException("Google account already registered");
                });

        // 3. Create AppUser
        AppUserEntity user = AppUserEntity.builder()
                .employeeId(
                        request.getEmployeeId() != null
                                ? UUID.fromString(request.getEmployeeId())
                                : null)
                .status(
                        request.getStatus() != null
                                ? request.getStatus()
                                : "ACTIVE")
                .createdAt(Instant.now())
                .build();

        user = appUserRepository.save(user);

        // 4. Create UserIdentity (GOOGLE)
        UserIdentityEntity identity = new UserIdentityEntity();
        identity.setUserId(user.getId());
        identity.setProvider("GOOGLE");
        identity.setProviderUid(request.getGoogleUid());
        identity.setEmail(request.getEmail());

        userIdentityRepository.save(identity);

        // 5. Emit event: iam.user.created
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(user.getId())
                .employeeId(user.getEmployeeId())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();

        userEventProducer.publishUserCreated(event);

        // 6. Response
        return new CreateUserResponse(user.getId().toString());
    }

    @Override
    public void updateStatus(UUID userId, UpdateUserStatusRequest request) {
    }

    @Override
    public void lockUser(UUID userId, LockUserRequest request) {
    }

    @Override
    public UnlockUserResponse unlockUser(UUID userId, UnlockUserRequest request) {
        return new UnlockUserResponse(
                true,
                userId.toString(),
                "ACTIVE",
                Instant.now(),
                "system");
    }

    @Override
    public ForcePasswordResetResponse forcePasswordReset(
            UUID userId,
            ForcePasswordResetRequest request) {

        return new ForcePasswordResetResponse(
                true,
                userId.toString(),
                true,
                Instant.now());
    }

    @Override
    public void assignRoles(UUID userId, AssignRolesRequest request) {
    }

    @Override
    public UserDetailResponse getUserDetail(UUID userId) {
        UserDetailResponse response = new UserDetailResponse();
        response.setUserId(userId.toString());
        response.setUsername("demo_user");
        response.setFullName("Demo User");
        response.setEmail("demo@company.com");
        response.setPhone("0123456789");
        response.setStatus("ACTIVE");
        response.setCreatedAt(Instant.now());
        response.setLastLoginAt(null);
        return response;
    }

    @Override
    public UserRolesResponse getUserRoles(UUID userId) {
        UserRolesResponse response = new UserRolesResponse();
        response.setUserId(userId.toString());
        response.setRoles(List.of());
        return response;
    }

    @Override
    public UserDataScopeResponse getUserDataScopes(UUID userId) {
        UserDataScopeResponse response = new UserDataScopeResponse();
        response.setUserId(userId.toString());
        response.setDataScopes(List.of());
        return response;
    }

}
