package com.atlas_erp.inventory_service.Dto;

import lombok.Data;

@Data
public class ItemDto {
    private String id;
    private String itemCode;
    private String itemName;
    private String unit;
    private String categoryId;
    private String itemType;
    private String status;
}
