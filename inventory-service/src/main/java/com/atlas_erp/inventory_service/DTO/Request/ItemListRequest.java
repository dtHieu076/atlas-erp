package com.atlas_erp.inventory_service.DTO.Request;

import lombok.Data;

@Data
public class ItemListRequest {
    private String keyword;
    private String categoryId;
    private String itemType;
    private String status;
    private Integer page;
    private Integer pageSize;
}
