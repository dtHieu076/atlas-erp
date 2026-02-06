package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Cache.serviceCache.InventoryCacheService;
import com.atlas_erp.inventory_service.Dto.Request.StockDocument.ApproveStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockDocument.CancelStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockDocument.CreateStockDocumentRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.CancelStockDocumentResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.CreateStockDocumentResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.StockDocumentDetailResponse;
import com.atlas_erp.inventory_service.Entity.StockDocumentEntity;
import com.atlas_erp.inventory_service.Entity.StockDocumentItemEntity;
import com.atlas_erp.inventory_service.Event.dtoEvent.StockDocumentCancelledEvent;
import com.atlas_erp.inventory_service.Event.serviceEvent.StockDocumentEventService;
import com.atlas_erp.inventory_service.Repository.StockDocumentItemRepository;
import com.atlas_erp.inventory_service.Repository.StockDocumentRepository;
import com.atlas_erp.inventory_service.Service.StockDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import com.atlas_erp.inventory_service.Util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StockDocumentServiceImpl implements StockDocumentService {

    // private final StockTransactionRepository stockTransactionRepository;
    private final StockDocumentRepository stockDocumentRepository;
    private final StockDocumentItemRepository stockDocumentItemRepository;
    private final InventoryCacheService inventoryCacheService;

    @Override
    @Transactional
    public CreateStockDocumentResponse createStockDocument(CreateStockDocumentRequest request) {
        log.info("Bắt đầu tạo chứng từ kho: Loại = {}, Kho = {}", request.getDocType(), request.getWarehouseId());

        // 1. Validate sơ bộ (Bạn có thể gọi Organization/IAM Service ở đây)
        validateExternalResources(request);

        // 2. Lưu thông tin Header (Phiếu kho)
        StockDocumentEntity document = new StockDocumentEntity();
        document.setDocumentType(request.getDocType());
        document.setStatus("DRAFT"); // Luôn để DRAFT để chờ kiểm tra thực tế
        document.setCreatedAt(Instant.now());

        // Lưu ý: StockDocumentEntity hiện tại của bạn thiếu trường WarehouseId và
        // CreatedBy.
        // Bạn nên cân nhắc bổ sung vào Entity sau này để truy vấn tốt hơn.

        StockDocumentEntity savedDoc = stockDocumentRepository.save(document);

        // 3. Lưu danh sách Item chi tiết
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Danh sách vật tư không được để trống");
        }

        List<StockDocumentItemEntity> docItems = request.getItems().stream().map(itemReq -> {
            StockDocumentItemEntity item = new StockDocumentItemEntity();
            item.setStockDocumentId(savedDoc.getId());
            item.setItemId(UUID.fromString(itemReq.getItemId()));
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            return item;
        }).collect(Collectors.toList());

        stockDocumentItemRepository.saveAll(docItems);

        // 4. Trả về kết quả
        CreateStockDocumentResponse response = new CreateStockDocumentResponse();
        response.setId(savedDoc.getId().toString());

        log.info("Tạo chứng từ thành công: ID = {}", response.getId());
        return response;
    }

    private void validateExternalResources(CreateStockDocumentRequest request) {
        // Mock validate: Chỉ kiểm tra format UUID
        try {
            UUID.fromString(request.getWarehouseId());
            if (request.getRelatedDeptId() != null)
                UUID.fromString(request.getRelatedDeptId());
            if (request.getCreatedBy() != null)
                UUID.fromString(request.getCreatedBy());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Định dạng ID không hợp lệ (Organization/IAM)");
        }

        // organizationClient.getDepartment(request.getRelatedDeptId());
        // iamClient.getUser(request.getCreatedBy());
    }

    @Override // Chưa cần gấp
    public void approveStockDocument(UUID stockDocumentId, ApproveStockDocumentRequest request) {
    }

    @Override
    public StockDocumentDetailResponse getStockDocumentDetail(UUID stockDocumentId) {
        log.info("Truy vấn chi tiết chứng từ kho ID: {}", stockDocumentId);

        // 1. Kiểm tra trong Redis Cache trước
        StockDocumentDetailResponse cachedResponse = inventoryCacheService.getDetailCache(stockDocumentId);
        if (cachedResponse != null) {
            log.info("Cache Hit - Trả về dữ liệu từ Redis cho ID: {}", stockDocumentId);
            return cachedResponse;
        }

        // 2. Cache Miss - Truy vấn Database
        log.info("Cache Miss - Truy vấn Database cho ID: {}", stockDocumentId);

        // Tìm Header
        StockDocumentEntity document = stockDocumentRepository.findById(stockDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chứng từ với ID: " + stockDocumentId));

        // Tìm Items chi tiết
        List<StockDocumentItemEntity> itemEntities = stockDocumentItemRepository.findByStockDocumentId(stockDocumentId);

        // 3. Mapping dữ liệu sang DTO
        StockDocumentDetailResponse response = new StockDocumentDetailResponse();
        response.setStockDocumentId(document.getId().toString());
        response.setDocumentType(document.getDocumentType());
        response.setStatus(document.getStatus());
        response.setCreatedAt(document.getCreatedAt() != null ? Date.from(document.getCreatedAt()) : null);
        response.setCreatedBy(document.getApprovedBy() != null ? document.getApprovedBy().toString() : "N/A");
        response.setWarehouseId("WH-DEFAULT"); // Placeholder

        List<StockDocumentDetailResponse.StockDocumentItem> items = itemEntities.stream().map(itemEntity -> {
            StockDocumentDetailResponse.StockDocumentItem itemDto = new StockDocumentDetailResponse.StockDocumentItem();
            itemDto.setItemId(itemEntity.getItemId().toString());
            itemDto.setQuantity(itemEntity.getQuantity());
            itemDto.setBatchId(null); // Bổ sung sau nếu có bảng Batch
            itemDto.setUnit("PCS"); // Bổ sung sau nếu lấy từ bảng Item Master
            return itemDto;
        }).collect(Collectors.toList());

        response.setItems(items);

        // 4. Lưu kết quả vào Redis Cache cho lần truy cập sau
        inventoryCacheService.putDetailCache(stockDocumentId, response);

        return response;
    }

    private final StockDocumentEventService eventService; // Inject thêm vào

    @Override
    @Transactional
    public CancelStockDocumentResponse cancelStockDocument(UUID stockDocumentId, CancelStockDocumentRequest request) {
        log.info("Yêu cầu hủy chứng từ kho ID: {}", stockDocumentId);

        // 1. Lấy ID người dùng hiện tại từ Token
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thông tin người dùng trong Token. Vui lòng đăng nhập lại."));

        // 2. Tìm chứng từ trong DB
        StockDocumentEntity document = stockDocumentRepository.findById(stockDocumentId)
                .orElseThrow(() -> new RuntimeException("Chứng từ không tồn tại: " + stockDocumentId));

        // 3. Validate trạng thái (Chỉ cho phép hủy phiếu NHÁP)
        if (!"DRAFT".equalsIgnoreCase(document.getStatus())) {
            throw new IllegalStateException("Không thể hủy chứng từ đã được phê duyệt hoặc đã hủy.");
        }

        // 4. Cập nhật Entity
        document.setStatus("CANCELLED");
        document.setApprovedBy(currentUserId); // Ghi nhận người thực hiện hủy
        // document.setApprovedAt(Instant.now()); // Có thể dùng trường này lưu thời
        // điểm hủy

        stockDocumentRepository.save(document);

        // 5. Xóa Cache chi tiết chứng từ ngay lập tức
        inventoryCacheService.evictDetailCache(stockDocumentId);

        // 6. Phát Event cho các Service liên quan (Kế toán, Sản xuất...)
        StockDocumentCancelledEvent cancelledEvent = StockDocumentCancelledEvent.builder()
                .stockDocumentId(document.getId())
                .documentType(document.getDocumentType())
                .reason(request.getReason())
                .cancelledBy(currentUserId)
                .cancelledAt(Instant.now())
                .build();

        eventService.publishCancelledEvent(cancelledEvent);

        // 7. Map Response trả về Client
        CancelStockDocumentResponse response = new CancelStockDocumentResponse();
        response.setSuccess(true);
        response.setStockDocumentId(document.getId().toString());
        response.setStatus(document.getStatus());
        response.setCancelledAt(new Date());
        response.setCancelledBy(currentUserId.toString());

        log.info("Hủy chứng từ thành công bởi user: {}", currentUserId);
        return response;
    }

}
