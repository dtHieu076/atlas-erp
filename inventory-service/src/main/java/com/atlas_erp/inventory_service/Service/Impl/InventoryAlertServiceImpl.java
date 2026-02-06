package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.InventoryAlert.InventoryAlertQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.InventoryAlert.InventoryAlertResponse;
import com.atlas_erp.inventory_service.Dto.Response.InventoryAlert.InventoryAlertResponse.InventoryAlertDto;
import com.atlas_erp.inventory_service.Entity.InventoryAlertEntity;
import com.atlas_erp.inventory_service.Repository.InventoryAlertRepository;
import com.atlas_erp.inventory_service.Service.InventoryAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryAlertServiceImpl implements InventoryAlertService {

    private final InventoryAlertRepository inventoryAlertRepository;

    @Override
    public InventoryAlertResponse getInventoryAlerts(InventoryAlertQueryRequest request) {
        log.info("Fetching inventory alerts with filters: warehouseId={}, type={}",
                request.getWarehouseId(), request.getAlertType());

        // 1. Chuẩn bị tham số lọc
        UUID warehouseId = (request.getWarehouseId() != null && !request.getWarehouseId().isEmpty())
                ? UUID.fromString(request.getWarehouseId())
                : null;

        String alertType = (request.getAlertType() != null && !request.getAlertType().isEmpty())
                ? request.getAlertType()
                : null;

        // 2. Query từ Database
        List<InventoryAlertEntity> entities = inventoryAlertRepository.findAlerts(warehouseId, alertType);

        // 3. Map Entity sang DTO
        List<InventoryAlertDto> dtos = entities.stream().map(entity -> {
            InventoryAlertDto dto = new InventoryAlertDto();
            dto.setAlertId(entity.getId().toString());
            dto.setAlertType(entity.getAlertType());
            dto.setWarehouseId(entity.getWarehouseId().toString());
            dto.setItemId(entity.getItemId().toString());
            dto.setCurrentQuantity(entity.getCurrentQuantity());
            dto.setThreshold(entity.getThreshold());
            dto.setGeneratedAt(Date.from(entity.getGeneratedAt()));
            return dto;
        }).collect(Collectors.toList());

        // 4. Trả về kết quả
        InventoryAlertResponse response = new InventoryAlertResponse();
        response.setAlerts(dtos);

        log.info("Found {} alerts", dtos.size());
        return response;
    }
}