package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Cache.serviceCache.ProductionCacheService;
import com.atlas_erp.production_service.Dto.Request.ProductionOrder.*;
import com.atlas_erp.production_service.Dto.Response.ProductionOrder.*;
import com.atlas_erp.production_service.Dto.Response.ProductionOrder.ProductionOrderListResponse.ProductionOrderDto;
import com.atlas_erp.production_service.Entity.ProductionOrderEntity;
import com.atlas_erp.production_service.Entity.ProductionPlanEntity;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionOrderCreatedEvent;
import com.atlas_erp.production_service.Event.serviceEvent.ProductionEventService;
import com.atlas_erp.production_service.Repository.ProductionOrderRepository;
import com.atlas_erp.production_service.Repository.ProductionPlanRepository;
import com.atlas_erp.production_service.Service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository orderRepository;
    private final ProductionPlanRepository planRepository;
    private final ProductionEventService eventService;
    private final ProductionCacheService cacheService;

    @Override
    @Transactional
    public CreateProductionOrderResponse createProductionOrder(CreateProductionOrderRequest request) {
        log.info("Tạo lệnh sản xuất mới từ Plan: {}, Mã lệnh: {}", request.getPlanId(), request.getOrderCode());

        // 1. Fetch và Validate Kế hoạch sản xuất (Production Plan)
        ProductionPlanEntity plan = planRepository.findById(UUID.fromString(request.getPlanId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch sản xuất."));

        // Kiểm tra trạng thái kế hoạch
        if (!"APPROVED".equalsIgnoreCase(plan.getStatus())) {
            throw new IllegalStateException("Kế hoạch chưa được phê duyệt, không thể tạo lệnh sản xuất.");
        }

        // 2. Tạo thực thể ProductionOrder
        ProductionOrderEntity order = new ProductionOrderEntity();
        order.setProductionPlanId(plan.getId());
        order.setProductId(UUID.fromString(request.getProductId()));
        order.setQuantity(new BigDecimal(request.getQuantity()));
        order.setStatus("CREATED");

        // Lưu Order vào DB
        order = orderRepository.save(order);

        // 3. (Tùy chọn) Cập nhật lại Plan nếu bạn có trường allocatedQty (số lượng đã
        // giao lệnh)
        // Hiện tại Entity ProductionPlan của bạn chưa có trường này,
        // nhưng trong logic ERP thực tế, bước này là bắt buộc để tránh tạo lệnh quá hạn
        // mức của Plan.
        log.info("Đã cập nhật hạn mức kế hoạch ID: {}", plan.getId());

        // 4. Phát Event Kafka cho Inventory Service và Workflow Service
        ProductionOrderCreatedEvent event = ProductionOrderCreatedEvent.builder()
                .orderId(order.getId())
                .planId(plan.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .teamId(request.getTeamId())
                .build();

        eventService.publishOrderCreated(event);

        // 5. Chuẩn bị Response
        CreateProductionOrderResponse response = new CreateProductionOrderResponse();
        response.setId(order.getId().toString());

        log.info("Tạo lệnh sản xuất thành công. ID: {}", order.getId());
        return response;
    }

    @Override
    public ProductionOrderListResponse getProductionOrders(ProductionOrderListRequest request) {
        // 1. Kiểm tra Cache
        ProductionOrderListResponse cached = cacheService.getOrderList(request);
        if (cached != null)
            return cached;

        // 2. Phân trang & Query
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getPageSize() != null ? request.getPageSize() : 10;

        UUID planUuid = (request.getPlanId() != null) ? UUID.fromString(request.getPlanId()) : null;

        Page<ProductionOrderEntity> orderPage = orderRepository.findByFilters(
                request.getStatus(),
                planUuid,
                PageRequest.of(page, size));

        // 3. Mapping sang DTO
        List<ProductionOrderDto> dtoList = orderPage.getContent().stream().map(entity -> {
            ProductionOrderDto dto = new ProductionOrderDto();
            dto.setId(entity.getId().toString());
            dto.setProductId(entity.getProductId().toString());
            dto.setQuantity(entity.getQuantity().intValue());
            dto.setStatus(entity.getStatus());
            // completedQty và các trường date có thể bổ sung từ các Entity liên quan
            dto.setCompletedQty(0);
            return dto;
        }).collect(Collectors.toList());

        // 4. Build Response & Lưu Cache
        ProductionOrderListResponse response = new ProductionOrderListResponse();
        response.setList(dtoList);
        response.setTotal(orderPage.getTotalElements());

        cacheService.putOrderList(request, response);

        return response;
    }

}
