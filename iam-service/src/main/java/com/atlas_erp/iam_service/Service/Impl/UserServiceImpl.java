package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Cache.serviceCache.UserCacheService;
import com.atlas_erp.iam_service.Dto.UserDataScopeDto;
import com.atlas_erp.iam_service.Dto.UserRoleDto;
import com.atlas_erp.iam_service.Dto.Request.User.*;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.User.*;
import com.atlas_erp.iam_service.Entity.AppUserEntity;
import com.atlas_erp.iam_service.Entity.RoleEntity;
import com.atlas_erp.iam_service.Entity.UserDataScopeEntity;
import com.atlas_erp.iam_service.Entity.UserIdentityEntity;
import com.atlas_erp.iam_service.Entity.UserRoleEntity;
import com.atlas_erp.iam_service.Event.dtoEvent.UserAccountLockedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserAccountUnlockedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserCreatedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserRoleAssignedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserStatusUpdatedEvent;
import com.atlas_erp.iam_service.Event.serviceEvent.UserEventService;
import com.atlas_erp.iam_service.Repository.AppUserRepository;
import com.atlas_erp.iam_service.Repository.RoleRepository;
import com.atlas_erp.iam_service.Repository.UserIdentityRepository;
import com.atlas_erp.iam_service.Repository.UserRoleRepository;
import com.atlas_erp.iam_service.Repository.UserDataScopeRepository;
import com.atlas_erp.iam_service.Service.UserService;
import com.atlas_erp.iam_service.util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Lombok sẽ tạo Constructor cho tất cả các field 'final'
public class UserServiceImpl implements UserService {

    private final UserIdentityRepository userIdentityRepository;
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserEventService userEventService;
    private final UserCacheService userCacheService;
    private final UserDataScopeRepository userDataScopeRepository;

    @Override
    public UserListResponse searchUsers(UserSearchRequest request) {
        return new UserListResponse();
    }

    @Override
    @Transactional
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

        userEventService.publishUserCreated(event);

        // 6. Response
        return new CreateUserResponse(user.getId().toString());
    }

    @Override
    @Transactional
    public BaseSuccessResponse updateStatus(UUID userId, UpdateUserStatusRequest request) {

        // 1. Validate input
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new IllegalArgumentException("status is required");
        }

        // 2. Load AppUserEntity
        AppUserEntity user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 3. Update status
        user.setStatus(request.getStatus());

        // 4. Save
        appUserRepository.save(user);

        // 5. Publish event
        UserStatusUpdatedEvent event = UserStatusUpdatedEvent.builder()
                .userId(user.getId())
                .status(user.getStatus())
                .updatedAt(Instant.now())
                .build();

        userEventService.publishUserStatusUpdated(event);

        // 6. Response
        return BaseSuccessResponse.success();
    }

    @Override
    @Transactional
    public BaseSuccessResponse lockUser(UUID userId, LockUserRequest request) {
        // 1. Tìm thông tin danh tính của User
        // Giả sử mỗi user có 1 identity chính, nếu có nhiều bạn cần findByUserIdIn hoặc
        // logic tương đương
        UserIdentityEntity identity = userIdentityRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User identity not found for ID: " + userId));

        // 2. Cập nhật trạng thái khóa
        identity.setLocked(true);
        identity.setLockReason(request.getReason());
        identity.setLockedAt(Instant.now());

        userIdentityRepository.save(identity);

        // 2. Tạo Event với tên có ý nghĩa
        // Trong file UserServiceImpl.java
        UserAccountLockedEvent event = UserAccountLockedEvent.builder()
                .userId(userId)
                .email(identity.getEmail())
                .reason(request.getReason())
                // Lấy giá trị từ Optional, nếu không có thì trả về null
                .lockedBy(SecurityUtils.getCurrentUserId().orElse(null))
                .lockedAt(Instant.now())
                .build();

        // 3. Publish
        userEventService.publishUserLocked(event);

        return BaseSuccessResponse.success();
    }

    @Override
    @Transactional
    public UnlockUserResponse unlockUser(UUID userId, UnlockUserRequest request) {
        // 1. Load UserIdentityEntity
        UserIdentityEntity identity = userIdentityRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User identity not found for ID: " + userId));

        // 2. Lấy ID người thực hiện (Admin) từ SecurityUtils
        UUID adminId = SecurityUtils.getCurrentUserId().orElse(null);

        // 3. Cập nhật trạng thái mở khóa
        identity.setLocked(false);
        identity.setLockReason(null); // Xóa lý do khóa cũ
        // Nếu identity có các trường unlockedAt, unlockedBy thì set tại đây
        // identity.setUnlockedAt(Instant.now());
        // identity.setUnlockedBy(adminId);

        userIdentityRepository.save(identity);

        // 4. Publish Event cho Audit Service
        UserAccountUnlockedEvent event = UserAccountUnlockedEvent.builder()
                .userId(userId)
                .email(identity.getEmail())
                .reason(request.getReason())
                .unlockedBy(adminId)
                .unlockedAt(Instant.now())
                .build();

        userEventService.publishUserUnlocked(event);

        // 5. Trả về Response
        return UnlockUserResponse.builder()
                .success(true)
                .userId(userId.toString())
                .status("ACTIVE")
                .unlockedAt(Instant.now())
                .unlockedBy(adminId != null ? adminId.toString() : "system")
                .build();
    }

    @Override // Không cần API này vì đăng nhập bằng google
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
    @Transactional
    public BaseSuccessResponse assignRoles(UUID userId, AssignRolesRequest request) {
        // 1. Kiểm tra User có tồn tại không
        if (!appUserRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }

        // 2. Chuyển đổi String ID sang UUID và Validate Roles
        List<UUID> roleUUIDs = request.getRoleIds().stream()
                .map(UUID::fromString)
                .toList();

        List<RoleEntity> roles = roleRepository.findAllById(roleUUIDs);
        if (roles.size() != roleUUIDs.size()) {
            throw new IllegalArgumentException("One or more roles do not exist");
        }

        // 3. Xóa các gán quyền cũ (Dọn dẹp)
        userRoleRepository.deleteByUserId(userId);

        // 4. Lưu các gán quyền mới
        List<UserRoleEntity> newUserRoles = roles.stream()
                .map(role -> {
                    UserRoleEntity ur = new UserRoleEntity();
                    ur.setUserId(userId);
                    ur.setRoleId(role.getId());
                    return ur;
                })
                .toList();

        userRoleRepository.saveAll(newUserRoles);

        // --- ĐOẠN SỬA/THÊM MỚI ---
        // 5. Xóa Cache Roles cũ trong Redis để lần sau getUserRoles sẽ lấy data mới từ
        // DB
        userCacheService.evictRoles(userId);
        // -------------------------

        // 6. Phát Event (Để các service khác như Cache Service của họ hoặc Audit nhận
        // biết)
        UserRoleAssignedEvent event = UserRoleAssignedEvent.builder()
                .userId(userId)
                .roleCodes(roles.stream().map(RoleEntity::getRoleCode).toList())
                .assignedBy(SecurityUtils.getCurrentUserId().orElse(null))
                .build();

        userEventService.publishUserRoleAssigned(event);

        return BaseSuccessResponse.success();
    }

    @Override
    public UserDetailResponse getUserDetail(UUID userId) {
        // 1. Thử lấy từ Cache trước
        UserDetailResponse cachedDetail = userCacheService.getUserDetailCache(userId);
        if (cachedDetail != null) {
            return cachedDetail;
        }

        // 2. Cache Miss - Load thông tin cơ bản từ AppUser
        AppUserEntity user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // 3. Load thông tin định danh từ UserIdentity
        UserIdentityEntity identity = userIdentityRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Identity not found for user: " + userId));

        // 4. Map dữ liệu vào Response
        UserDetailResponse response = UserDetailResponse.builder()
                .userId(user.getId().toString())
                .employeeId(user.getEmployeeId())
                .username(identity.getEmail())
                .email(identity.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(null)
                .build();

        // 5. Lưu vào Cache cho lần gọi sau
        userCacheService.putUserDetailCache(userId, response);

        return response;
    }

    @Override
    public UserRolesResponse getUserRoles(UUID userId) {
        // 1. Thử lấy từ Cache Service
        UserRolesResponse cachedRoles = userCacheService.getUserRoles(userId);
        if (cachedRoles != null) {
            return cachedRoles;
        }

        // 2. Cache miss -> Xuống Database
        if (!appUserRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }

        List<RoleEntity> roles = userRoleRepository.findAllRolesByUserId(userId);

        // 3. Mapping
        List<UserRoleDto> roleDtos = roles.stream()
                .map(role -> {
                    UserRoleDto dto = new UserRoleDto();
                    dto.setRoleId(role.getId().toString());
                    dto.setRoleCode(role.getRoleCode());
                    dto.setRoleName(role.getRoleName());
                    return dto;
                })
                .toList();

        UserRolesResponse response = new UserRolesResponse();
        response.setUserId(userId.toString());
        response.setRoles(roleDtos);

        // 4. Đẩy ngược lại vào Cache Service cho lần sau
        userCacheService.putUserRoles(userId, response);

        return response;
    }

    @Override
    public UserDataScopeResponse getUserDataScopes(UUID userId) {
        // 1. Kiểm tra Cache trước (Performance First)
        UserDataScopeResponse cachedResponse = userCacheService.getUserDataScopes(userId);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        // 2. Kiểm tra sự tồn tại của User trong AppUser
        if (!appUserRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        // 3. Truy vấn Database lấy danh sách Scope Entities
        List<UserDataScopeEntity> scopeEntities = userDataScopeRepository.findByUserId(userId);

        // 4. Chuyển đổi sang DTO (IAM không quản lý Name, để Gateway/BFF làm)
        List<UserDataScopeDto> scopeDtos = scopeEntities.stream()
                .map(entity -> {
                    UserDataScopeDto dto = new UserDataScopeDto();
                    dto.setScopeType(entity.getScopeType().name());
                    dto.setScopeId(entity.getScopeId().toString());
                    // Trả về null để Gateway biết cần phải Resolve Name từ Organization Service
                    dto.setScopeName(null);
                    return dto;
                })
                .toList();

        // 5. Khởi tạo Response Object
        UserDataScopeResponse response = new UserDataScopeResponse();
        response.setUserId(userId.toString());
        response.setDataScopes(scopeDtos);

        // 6. Lưu vào Redis trước khi trả về
        userCacheService.putUserDataScopes(userId, response);

        return response;
    }

}
