package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Cache.serviceCache.InventoryCacheService;
import com.atlas_erp.inventory_service.Dto.WarehouseDto;
import com.atlas_erp.inventory_service.Dto.Request.Warehouse.WarehouseListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Warehouse.WarehouseListResponse;
import com.atlas_erp.inventory_service.Entity.WarehouseEntity;
import com.atlas_erp.inventory_service.Repository.WarehouseRepository;
import com.atlas_erp.inventory_service.Service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final InventoryCacheService inventoryCacheService;

    @Override
    public WarehouseListResponse findWarehousesByPlant(WarehouseListRequest request) {
        log.info("Truy vấn danh sách kho cho Plant: {}", request.getPlantId());

        // 1. Thử lấy từ Cache
        WarehouseListResponse cachedResponse = inventoryCacheService.getWarehouseListCache(request);
        if (cachedResponse != null) {
            log.info("Cache Hit - Trả về danh sách kho từ Redis");
            return cachedResponse;
        }

        // 2. Cache Miss - Query Database với phân trang
        int page = (request.getPage() != null) ? request.getPage() : 0;
        int size = (request.getPageSize() != null) ? request.getPageSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        UUID plantUuid = UUID.fromString(request.getPlantId());
        Page<WarehouseEntity> warehousePage = warehouseRepository.findByPlantId(plantUuid, pageable);

        // 3. Mapping Entity sang Dto
        List<WarehouseDto> dtoList = warehousePage.getContent().stream().map(entity -> {
            WarehouseDto dto = new WarehouseDto();
            dto.setId(entity.getId().toString());
            dto.setCode(entity.getCode());
            dto.setName(entity.getName());
            dto.setPlantId(entity.getPlantId() != null ? entity.getPlantId().toString() : null);
            dto.setStatus("ACTIVE"); // Giả định ACTIVE vì Entity chưa có status
            // managerId hiện tại chưa có trong Entity, có thể để null hoặc bổ sung sau
            dto.setManagerId(null); 
            return dto;
        }).collect(Collectors.toList());

        // 4. Build Response
        WarehouseListResponse response = new WarehouseListResponse();
        response.setList(dtoList);
        response.setTotal(warehousePage.getTotalElements());

        // 5. Lưu vào Cache
        inventoryCacheService.putWarehouseListCache(request, response);

        return response;
    }
}