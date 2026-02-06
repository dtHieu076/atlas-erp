package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Response.BatchTrace.BatchTraceResponse;
import com.atlas_erp.inventory_service.Entity.InventoryBatchEntity;
import com.atlas_erp.inventory_service.Entity.StockTransactionEntity;
import com.atlas_erp.inventory_service.Repository.InventoryBatchRepository;
import com.atlas_erp.inventory_service.Repository.StockTransactionRepository;
import com.atlas_erp.inventory_service.Service.BatchTraceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.kafka.common.errors.ResourceNotFoundException; // Hoặc custom exception của bạn

import java.util.UUID;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchTraceServiceImpl implements BatchTraceService {

    private final InventoryBatchRepository inventoryBatchRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    public BatchTraceResponse traceBatchHistory(UUID batchId) {
        log.info("Tracing history for batch: {}", batchId);

        // 1. Kiểm tra lô hàng (Batch) có tồn tại không
        InventoryBatchEntity batch = inventoryBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng ID: " + batchId));

        // 2. Truy vấn toàn bộ giao dịch kho liên quan đến batch này
        List<StockTransactionEntity> transactions = stockTransactionRepository
                .findByBatchIdOrderByTransactionDateAsc(batchId);

        // 3. Map dữ liệu sang TraceStep
        List<BatchTraceResponse.TraceStep> steps = transactions.stream().map(tx -> {
            BatchTraceResponse.TraceStep step = new BatchTraceResponse.TraceStep();

            // Mapping action dựa trên referenceType hoặc logic nghiệp vụ
            // VD: GRN (Goods Receipt Note) -> RECEIVED, GIN (Goods Issue Note) -> ISSUED
            step.setAction(tx.getReferenceType());

            step.setReferenceId(tx.getReferenceId() != null ? tx.getReferenceId().toString() : null);
            step.setWarehouseId(tx.getWarehouseId().toString());
            step.setQuantity(tx.getQuantity());

            // Convert Instant sang Date cho DTO
            step.setOccurredAt(Date.from(tx.getTransactionDate()));

            return step;
        }).collect(Collectors.toList());

        // 4. Đóng gói Response
        BatchTraceResponse response = new BatchTraceResponse();
        response.setBatchId(batch.getId().toString());
        response.setItemId(batch.getItemId().toString());
        response.setTraceSteps(steps);

        log.info("Found {} trace steps for batch {}", steps.size(), batch.getBatchCode());
        return response;
    }
}