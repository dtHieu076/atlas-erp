package com.atlas_erp.iam_service.Repository;

import com.atlas_erp.iam_service.Entity.UserIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentityEntity, UUID> {

    // Đổi từ findByProviderAndProviderUid thành:
    Optional<UserIdentityEntity> findByGoogleId(String googleId);

    // Nếu bạn cần tìm theo email:
    Optional<UserIdentityEntity> findByEmail(String email);
}