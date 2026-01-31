package com.atlas_erp.inventory_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.atlas_erp.inventory_service.Entity.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {

    @Query("""
                SELECT i FROM ItemEntity i
                WHERE (:keyword IS NULL OR i.name LIKE %:keyword%)
                  AND (:itemType IS NULL OR i.itemType = :itemType)
            """)
    Page<ItemEntity> search(
            String keyword,
            String itemType,
            Pageable pageable);
}
