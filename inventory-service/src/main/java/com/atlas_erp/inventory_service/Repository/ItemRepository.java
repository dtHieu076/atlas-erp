package com.atlas_erp.inventory_service.Repository;

import com.atlas_erp.inventory_service.Entity.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {

        @Query("""
                            SELECT i FROM ItemEntity i
                            WHERE (:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                   OR LOWER(i.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
                              AND (:itemType IS NULL OR i.itemType = :itemType)
                        """)
        Page<ItemEntity> search(
                        @Param("keyword") String keyword,
                        @Param("itemType") String itemType,
                        Pageable pageable);
}