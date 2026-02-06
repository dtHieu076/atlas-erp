package com.atlas_erp.inventory_service.Repository;

import com.atlas_erp.inventory_service.Entity.StockDocumentItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockDocumentItemRepository extends JpaRepository<StockDocumentItemEntity, UUID> {

    /**
     * Lấy danh sách tất cả các mặt hàng thuộc về một chứng từ kho cụ thể
     * Thường dùng để hiển thị chi tiết phiếu kho hoặc phục vụ logic phê duyệt
     */
    List<StockDocumentItemEntity> findByStockDocumentId(UUID stockDocumentId);

    /**
     * Xóa tất cả các dòng hàng của một chứng từ
     * Thường dùng khi hủy hoặc chỉnh sửa phiếu ở trạng thái DRAFT
     */
    void deleteByStockDocumentId(UUID stockDocumentId);
}