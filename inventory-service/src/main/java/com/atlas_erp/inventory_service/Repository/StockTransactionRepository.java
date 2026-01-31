package com.atlas_erp.inventory_service.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.atlas_erp.inventory_service.Entity.StockTransactionEntity;

@Repository
public interface StockTransactionRepository
        extends JpaRepository<StockTransactionEntity, UUID> {

    List<StockTransactionEntity> findByWarehouseId(UUID warehouseId);
}
