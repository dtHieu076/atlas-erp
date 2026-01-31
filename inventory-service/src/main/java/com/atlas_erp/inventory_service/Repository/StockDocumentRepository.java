package com.atlas_erp.inventory_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.atlas_erp.inventory_service.Entity.StockDocumentEntity;

@Repository
public interface StockDocumentRepository
        extends JpaRepository<StockDocumentEntity, UUID> {
}
