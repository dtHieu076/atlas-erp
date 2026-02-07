package com.atlas_erp.accounting_service.Repository;

import com.atlas_erp.accounting_service.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    @Query("SELECT a FROM AccountEntity a WHERE " +
            "(:type IS NULL OR a.accountType = :type) AND " +
            "(:parent IS NULL OR a.parentId = :parent)")
    List<AccountEntity> findByFilters(@Param("type") String type, @Param("parent") UUID parent);
}