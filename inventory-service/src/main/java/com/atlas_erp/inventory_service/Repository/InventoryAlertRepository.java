package com.atlas_erp.inventory_service.Repository;

import com.atlas_erp.inventory_service.Entity.InventoryAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface InventoryAlertRepository extends JpaRepository<InventoryAlertEntity, UUID> {
    
    @Query("SELECT a FROM InventoryAlertEntity a WHERE " +
           "(:warehouseId IS NULL OR a.warehouseId = :warehouseId) AND " +
           "(:alertType IS NULL OR a.alertType = :alertType) " +
           "ORDER BY a.generatedAt DESC")
    List<InventoryAlertEntity> findAlerts(@Param("warehouseId") UUID warehouseId, 
                                          @Param("alertType") String alertType);
}