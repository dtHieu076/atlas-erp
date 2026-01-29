package com.atlas_erp.production_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class ProductionOrderListResponse {

    private List<ProductionOrderDto> list;
    private Long total;

    @Data
    public static class ProductionOrderDto {
        private String id;
        private String orderCode;
        private String productId;
        private Integer quantity;
        private Integer completedQty;
        private String status;
        private Date startDate;
        private Date endDate;
    }
}
