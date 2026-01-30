package com.atlas_erp.iam_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import com.atlas_erp.iam_service.Entity.UserDataScopeEntity;
import java.util.List;

@Repository
public interface UserDataScopeRepository
        extends JpaRepository<UserDataScopeEntity, UUID> {

    List<UserDataScopeEntity> findByUserId(UUID userId);
}
