package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.StockDocument.ApproveStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockDocument.CancelStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockDocument.CreateStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.CancelStockDocumentResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.CreateStockDocumentResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.StockDocumentDetailResponse;
// import com.atlas_erp.inventory_service.Entity.StockDocumentEntity;
// import com.atlas_erp.inventory_service.Repository.StockDocumentRepository;
// import com.atlas_erp.inventory_service.Repository.StockTransactionRepository;
import com.atlas_erp.inventory_service.Service.StockDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockDocumentServiceImpl implements StockDocumentService {

    // private final StockDocumentRepository stockDocumentRepository;
    // private final StockTransactionRepository stockTransactionRepository;

    @Override
    public CreateStockDocumentResponse createStockDocument(
            CreateStockDocumentRequest request) {

        CreateStockDocumentResponse response = new CreateStockDocumentResponse();
        response.setId(UUID.randomUUID().toString()); // id giả

        return response;
    }

    @Override
    public void approveStockDocument(UUID stockDocumentId, ApproveStockDocumentRequest request) {
    }

    @Override
    public StockDocumentDetailResponse getStockDocumentDetail(UUID stockDocumentId) {

        StockDocumentDetailResponse response = new StockDocumentDetailResponse();
        response.setStockDocumentId(stockDocumentId.toString());
        response.setDocumentType(null); // chưa xác định
        response.setWarehouseId(null);
        response.setStatus(null);
        response.setCreatedAt(null);
        response.setCreatedBy(null);
        response.setItems(List.of()); // list rỗng

        return response;
    }

    @Override
    public CancelStockDocumentResponse cancelStockDocument(
            UUID stockDocumentId,
            CancelStockDocumentRequest request) {

        CancelStockDocumentResponse response = new CancelStockDocumentResponse();
        response.setSuccess(true);
        response.setStockDocumentId(stockDocumentId.toString());
        response.setStatus("CANCELLED");
        response.setCancelledAt(null); // chưa set thời điểm
        response.setCancelledBy(null); // chưa xác định

        return response;
    }

}
