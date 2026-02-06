package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Cache.serviceCache.ProductionCacheService;
import com.atlas_erp.production_service.Dto.Request.ProductionPlan.*;
import com.atlas_erp.production_service.Dto.Response.ProductionPlan.*;
import com.atlas_erp.production_service.Entity.ProductEntity;
import com.atlas_erp.production_service.Entity.ProductionExecutionEntity;
import com.atlas_erp.production_service.Entity.ProductionOrderEntity;
import com.atlas_erp.production_service.Entity.ProductionPlanEntity;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionPlanApprovedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionPlanRejectedEvent;
import com.atlas_erp.production_service.Event.serviceEvent.ProductionEventService;
import com.atlas_erp.production_service.Repository.ProductRepository;
import com.atlas_erp.production_service.Repository.ProductionExecutionRepository;
import com.atlas_erp.production_service.Repository.ProductionOrderRepository;
import com.atlas_erp.production_service.Repository.ProductionPlanRepository;
import com.atlas_erp.production_service.Service.ProductionPlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductionPlanServiceImpl implements ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderRepository orderRepository;
    private final ProductionEventService eventService;
    private final ProductionExecutionRepository executionRepository;
    private final ProductionCacheService cacheService;

    @Override
    public CreateProductionPlanResponse createProductionPlan(CreateProductionPlanRequest request) {
        log.info("Khởi tạo kế hoạch sản xuất: {}", request.getPlanCode());

        // 1. Kiểm tra tồn tại
        if (productionPlanRepository.existsByPlanCode(request.getPlanCode())) {
            throw new RuntimeException("Mã kế hoạch đã tồn tại trên hệ thống.");
        }

        // 2. Mapping & Khởi tạo
        ProductionPlanEntity plan = new ProductionPlanEntity();
        plan.setPlanCode(request.getPlanCode());
        plan.setVersion(1);

        // Mapping các trường mới bổ sung
        plan.setProductId(UUID.fromString(request.getProductId()));
        plan.setQuantityPlanned(BigDecimal.valueOf(request.getQuantity()));
        plan.setQuantityAllocated(BigDecimal.ZERO); // Mới tạo chưa phân bổ cho Order nào

        plan.setPlantId(UUID.fromString(request.getPlantId()));
        plan.setDepartmentId(UUID.fromString(request.getDepartmentId()));
        plan.setNote(request.getNote());

        plan.setStartDate(request.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        plan.setEndDate(request.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        plan.setStatus("DRAFT");

        // 3. Lưu DB
        plan = productionPlanRepository.save(plan);

        CreateProductionPlanResponse response = new CreateProductionPlanResponse();
        response.setId(plan.getId().toString());

        return response;
    }

    @Override
    public ApproveProductionPlanResponse approveProductionPlan(UUID planId, ApproveProductionPlanRequest request) {
        log.info("Đang phê duyệt kế hoạch sản xuất ID: {} bởi Approver: {}", planId, request.getApproverId());

        // 1. Fetch Entity
        ProductionPlanEntity plan = productionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch sản xuất với ID: " + planId));

        // 2. Validate: Chỉ cho phép duyệt nếu đang ở trạng thái DRAFT
        if (!"DRAFT".equalsIgnoreCase(plan.getStatus())) {
            log.warn("Kế hoạch ID: {} không ở trạng thái DRAFT (Trạng thái hiện tại: {})", planId, plan.getStatus());
            return new ApproveProductionPlanResponse(false);
        }

        // 3. Update trạng thái và thông tin phê duyệt
        Instant now = Instant.now();
        plan.setStatus("APPROVED");
        plan.setApprovedBy(UUID.fromString(request.getApproverId()));
        plan.setApprovedAt(now);

        productionPlanRepository.save(plan);

        // 4. Phát sự kiện Kafka
        ProductionPlanApprovedEvent event = ProductionPlanApprovedEvent.builder()
                .planId(plan.getId())
                .planCode(plan.getPlanCode())
                .productId(plan.getProductId())
                .approvedBy(plan.getApprovedBy())
                .approvedAt(plan.getApprovedAt())
                .build();

        eventService.publishPlanApproved(event);

        log.info("Kế hoạch {} đã được phê duyệt thành công.", plan.getPlanCode());
        return new ApproveProductionPlanResponse(true);
    }

    @Override
    public RejectProductionPlanResponse rejectProductionPlan(UUID planId, RejectProductionPlanRequest request) {
        log.info("Đang thực hiện từ chối kế hoạch ID: {}. Lý do: {}", planId, request.getReason());

        // 1. Fetch Entity
        ProductionPlanEntity plan = productionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch ID: " + planId));

        // 2. Validate:
        // - Chỉ từ chối khi ở trạng thái DRAFT hoặc APPROVED nhưng CHƯA phát lệnh
        // (Allocated = 0)
        if (!"DRAFT".equalsIgnoreCase(plan.getStatus()) && !"APPROVED".equalsIgnoreCase(plan.getStatus())) {
            throw new IllegalStateException("Trạng thái hiện tại không cho phép từ chối.");
        }

        if (plan.getQuantityAllocated() != null && plan.getQuantityAllocated().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Kế hoạch đã phát lệnh sản xuất, không thể từ chối. Hãy đóng lệnh trước.");
        }

        // 3. Update trạng thái
        Instant now = Instant.now();
        plan.setStatus("REJECTED");
        plan.setNote(request.getReason()); // Lưu lý do vào note hoặc bổ sung field rejectedReason

        productionPlanRepository.save(plan);

        // 4. Phát Event
        eventService.publishPlanRejected(ProductionPlanRejectedEvent.builder()
                .planId(plan.getId())
                .reason(request.getReason())
                .rejectedAt(now)
                .build());

        // 5. Build Response
        // Giả sử rejectedBy lấy từ SecurityContext hoặc request (ở đây tạm để null theo
        // logic không gọi IAM)
        RejectProductionPlanResponse response = new RejectProductionPlanResponse();
        response.setSuccess(true);
        response.setProductionPlanId(plan.getId().toString());
        response.setStatus("REJECTED");
        response.setRejectedAt(Date.from(now));
        response.setRejectedBy("CURRENT_USER"); // Placeholder

        return response;
    }

    @Override
    public ProductionPlanDetailResponse getProductionPlanDetail(UUID planId) {
        // 1. Thử lấy từ Cache
        ProductionPlanDetailResponse cachedResponse = cacheService.getPlanDetail(planId);
        if (cachedResponse != null) {
            log.info("🚀 Cache Hit: Chi tiết kế hoạch {}", planId);
            return cachedResponse;
        }

        log.info("🐢 Cache Miss: Truy vấn DB và tính toán tiến độ cho kế hoạch {}", planId);

        // 2. Truy vấn DB: Lấy thông tin Kế hoạch & Sản phẩm
        ProductionPlanEntity plan = productionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Kế hoạch sản xuất không tồn tại"));

        ProductEntity product = productRepository.findById(plan.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong hệ thống sản xuất"));

        // 3. Tính toán tiến độ thực tế
        List<ProductionOrderEntity> orders = orderRepository.findByProductionPlanId(planId);
        List<UUID> orderIds = orders.stream().map(ProductionOrderEntity::getId).toList();

        BigDecimal totalGoodQty = BigDecimal.ZERO;
        if (!orderIds.isEmpty()) {
            // Lấy tổng số lượng đạt chuẩn từ các đợt thực thi
            totalGoodQty = executionRepository.findByProductionOrderIdIn(orderIds)
                    .stream()
                    .map(ProductionExecutionEntity::getGoodQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        int progress = 0;
        if (plan.getQuantityPlanned().compareTo(BigDecimal.ZERO) > 0) {
            progress = totalGoodQty.multiply(new BigDecimal(100))
                    .divide(plan.getQuantityPlanned(), 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        // 4. Mapping dữ liệu
        ProductionPlanDetailResponse response = new ProductionPlanDetailResponse();
        response.setProductionPlanId(plan.getId().toString());
        response.setPlanCode(plan.getPlanCode());
        response.setProductId(product.getId().toString());
        response.setProductName(product.getName());
        response.setUnit(product.getUnit());
        response.setQuantityPlanned(plan.getQuantityPlanned().intValue());
        response.setStartDate(Date.from(plan.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        response.setEndDate(Date.from(plan.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        response.setStatus(plan.getStatus());
        response.setProgressPercent(progress);

        // 5. Lưu vào Cache trước khi trả về
        cacheService.putPlanDetail(planId, response);

        return response;
    }
}
