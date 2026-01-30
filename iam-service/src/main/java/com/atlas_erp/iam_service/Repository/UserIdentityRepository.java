package com.atlas_erp.iam_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import com.atlas_erp.iam_service.Entity.UserIdentityEntity;
import java.util.Optional;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentityEntity, UUID> {

    Optional<UserIdentityEntity> findByProviderAndProviderUid(
            String provider,
            String providerUid);

    Optional<UserIdentityEntity> findByUserId(UUID userId);
}
