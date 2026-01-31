package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Request.StockDocument.ApproveStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockDocument.CancelStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockDocument.CreateStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.CancelStockDocumentResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.CreateStockDocumentResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.StockDocumentDetailResponse;
import com.atlas_erp.inventory_service.Service.StockDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/stock-documents")
@RequiredArgsConstructor
public class StockDocumentController {

    private final StockDocumentService stockDocumentService;

    @PostMapping
    public CreateStockDocumentResponse createStockDocument(
            @RequestBody CreateStockDocumentRequest request) {
        return stockDocumentService.createStockDocument(request);
    }

    @PostMapping("/{id}/approve")
    public void approveStockDocument(@PathVariable UUID id, @RequestBody ApproveStockDocumentRequest request) {
        stockDocumentService.approveStockDocument(id, request);
    }

    @GetMapping("/{id}")
    public StockDocumentDetailResponse getStockDocumentDetail(@PathVariable UUID id) {
        return stockDocumentService.getStockDocumentDetail(id);
    }

    @PostMapping("/{id}/cancel")
    public CancelStockDocumentResponse cancelStockDocument(@PathVariable UUID id, @RequestBody CancelStockDocumentRequest request) {
        return stockDocumentService.cancelStockDocument(id, request);
    }
}
