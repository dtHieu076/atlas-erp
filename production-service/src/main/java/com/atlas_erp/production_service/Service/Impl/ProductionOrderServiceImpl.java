package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Dto.Request.ProductionOrder.*;
import com.atlas_erp.production_service.Dto.Response.ProductionOrder.*;
// import com.atlas_erp.production_service.Entity.ProductionOrderEntity;
// import com.atlas_erp.production_service.Repository.ProductionOrderRepository;
import com.atlas_erp.production_service.Service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl implements ProductionOrderService {

    // private final ProductionOrderRepository productionOrderRepository;

    @Override
    public CreateProductionOrderResponse createProductionOrder(CreateProductionOrderRequest request) {
        return null;
    }

    @Override
    public ProductionOrderListResponse getProductionOrders(ProductionOrderListRequest request) {

        ProductionOrderListResponse response = new ProductionOrderListResponse();

        // không cần data thật
        response.setList(Collections.emptyList());

        // total giả
        response.setTotal(0L);

        return response;
    }

}
