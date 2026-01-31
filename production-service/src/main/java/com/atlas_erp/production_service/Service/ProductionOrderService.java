package com.atlas_erp.production_service.Service;

import com.atlas_erp.production_service.Dto.Request.ProductionOrder.*;
import com.atlas_erp.production_service.Dto.Response.ProductionOrder.*;

public interface ProductionOrderService {
    CreateProductionOrderResponse createProductionOrder(
            CreateProductionOrderRequest request);

    ProductionOrderListResponse getProductionOrders(
            ProductionOrderListRequest request);
}
