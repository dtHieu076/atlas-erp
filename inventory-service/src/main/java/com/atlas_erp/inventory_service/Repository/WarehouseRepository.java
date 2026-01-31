package com.atlas_erp.inventory_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.atlas_erp.inventory_service.Entity.WarehouseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {

    Page<WarehouseEntity> findByPlantId(UUID plantId, Pageable pageable);
}
