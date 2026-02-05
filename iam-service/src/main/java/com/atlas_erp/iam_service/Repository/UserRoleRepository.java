package com.atlas_erp.iam_service.Repository;

import com.atlas_erp.iam_service.Entity.RoleEntity;
import com.atlas_erp.iam_service.Entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {

    /**
     * Lấy danh sách roleId của user
     * Dùng cho:
     * - GET /me/permissions
     * - Authorization
     */
    @Query("select ur.roleId from UserRoleEntity ur where ur.userId = :userId")
    List<UUID> findRoleIdsByUserId(UUID userId);

    // Tìm tất cả các Role gắn với UserId này
    @Query("SELECT r FROM RoleEntity r JOIN UserRoleEntity ur ON r.id = ur.roleId WHERE ur.userId = :userId")
    List<RoleEntity> findAllRolesByUserId(@Param("userId") UUID userId);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    List<UserRoleEntity> findByUserId(UUID userId);
}
