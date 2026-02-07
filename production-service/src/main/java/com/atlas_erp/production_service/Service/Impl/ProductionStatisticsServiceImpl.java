package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Dto.Request.ProductionStatistics.ProductionStatisticsSummaryRequest;
import com.atlas_erp.production_service.Dto.Response.ProductionStatistics.ProductionStatisticsSummaryResponse;
import com.atlas_erp.production_service.Service.ProductionStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionStatisticsServiceImpl implements ProductionStatisticsService {

    // private final ProductionOrderRepository productionOrderRepository;
    // private final ProductionExecutionRepository productionExecutionRepository;
    // private final ProductionPlanRepository productionPlanRepository;

    @Override
    public ProductionStatisticsSummaryResponse getProductionStatisticsSummary(
            ProductionStatisticsSummaryRequest request) {

        log.info("Fetching production statistics summary: periodType={}, periodValue={}, plantId={}",
                request.getPeriodType(), request.getPeriodValue(), request.getPlantId());

        // ===== V1 MOCK LOGIC (sẽ thay bằng query thật) =====
        Integer totalPlanQty = 1000;
        Integer totalActualQty = 920;
        Integer totalDefectQty = 80;

        Integer totalOrders = 25;
        Integer onTimeOrders = 21;

        BigDecimal capacityUtilization = BigDecimal.valueOf(0.87);

        ProductionStatisticsSummaryResponse response = new ProductionStatisticsSummaryResponse();

        response.setTotalPlanQty(totalPlanQty);
        response.setTotalActualQty(totalActualQty);
        response.setTotalDefectQty(totalDefectQty);
        response.setTotalOrders(totalOrders);
        response.setOnTimeOrders(onTimeOrders);
        response.setCapacityUtilization(capacityUtilization);

        return response;
    }
}
