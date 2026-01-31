package com.atlas_erp.inventory_service.Service;

import java.util.UUID;

import com.atlas_erp.inventory_service.Dto.Request.StockDocument.*;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.*;

public interface StockDocumentService {

    /**
     * Tạo chứng từ kho (nhập / xuất / chuyển kho)
     */
    CreateStockDocumentResponse createStockDocument(CreateStockDocumentRequest request);

    /**
     * Duyệt chứng từ kho và ghi nhận tồn kho
     */
    void approveStockDocument(UUID stockDocumentId, ApproveStockDocumentRequest request);

    /**
     * Lấy chi tiết chứng từ kho
     */
    StockDocumentDetailResponse getStockDocumentDetail(UUID stockDocumentId);

    /**
     * Hủy chứng từ kho
     */
    CancelStockDocumentResponse cancelStockDocument(UUID stockDocumentId, CancelStockDocumentRequest request);
}
