package com.atlas_erp.inventory_service.Repository;

import com.atlas_erp.inventory_service.Entity.InventoryBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryBatchRepository extends JpaRepository<InventoryBatchEntity, UUID> {

    // Tìm kiếm nhanh lô hàng theo mã code (dùng khi quét Barcode/QRCode)
    Optional<InventoryBatchEntity> findByBatchCode(String batchCode);

    // Lấy tất cả các lô hàng hiện có của một vật tư cụ thể
    List<InventoryBatchEntity> findByItemId(UUID itemId);

    // Kiểm tra sự tồn tại của mã lô trước khi tạo mới
    boolean existsByBatchCode(String batchCode);
}