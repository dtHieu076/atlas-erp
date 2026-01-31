package com.atlas_erp.inventory_service.Dto;

import lombok.Data;

@Data
public class WarehouseDto {
    private String id;
    private String code;
    private String name;
    private String plantId;
    private String managerId;
    private String status;
}
