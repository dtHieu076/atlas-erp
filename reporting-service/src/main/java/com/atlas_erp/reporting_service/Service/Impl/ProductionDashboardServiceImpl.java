package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Cache.serviceCache.ReportCacheService;
import com.atlas_erp.reporting_service.Client.ProductionClient;
import com.atlas_erp.reporting_service.Dto.External.ProductionStatisticsResponse;
import com.atlas_erp.reporting_service.Dto.Request.ProductionDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.ProductionDashboardResponse;
import com.atlas_erp.reporting_service.Service.ProductionDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionDashboardServiceImpl implements ProductionDashboardService {

    private final ReportCacheService cacheService;
    private final ProductionClient productionClient;

    @Override
    public ProductionDashboardResponse getProductionDashboard(ProductionDashboardRequest request) {

        // 1. Kiểm tra Cache
        ProductionDashboardResponse cached = cacheService.getProductionDashboard(
                request.getPlantId(), request.getPeriodType(), request.getPeriodValue());
        if (cached != null)
            return cached;

        // 2. Gọi Production Service lấy số liệu tổng hợp
        ProductionStatisticsResponse stats = productionClient.getProductionSummary(
                request.getPeriodType(), request.getPeriodValue(), request.getPlantId());

        ProductionDashboardResponse response = new ProductionDashboardResponse();

        if (stats != null) {
            // Gán các giá trị cơ bản
            response.setTotalPlanQty(stats.getTotalPlanQty());
            response.setTotalActualQty(stats.getTotalActualQty());

            // 3. Logic tính toán các tỷ lệ (%)

            // Defect Rate = (Lỗi / Thực tế) * 100
            if (stats.getTotalActualQty() != null && stats.getTotalActualQty() > 0) {
                BigDecimal defectRate = BigDecimal.valueOf(stats.getTotalDefectQty())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(stats.getTotalActualQty()), 2, RoundingMode.HALF_UP);
                response.setDefectRate(defectRate);
            } else {
                response.setDefectRate(BigDecimal.ZERO);
            }

            // On-Time Rate = (Đơn hàng đúng hạn / Tổng đơn hàng) * 100
            if (stats.getTotalOrders() != null && stats.getTotalOrders() > 0) {
                BigDecimal onTimeRate = BigDecimal.valueOf(stats.getOnTimeOrders())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(stats.getTotalOrders()), 2, RoundingMode.HALF_UP);
                response.setOnTimeRate(onTimeRate);
            } else {
                response.setOnTimeRate(BigDecimal.ZERO);
            }

            // Utilization Rate (Tỷ lệ khai thác) - Lấy trực tiếp từ Service tính toán
            response.setUtilizationRate(
                    stats.getCapacityUtilization() != null ? stats.getCapacityUtilization() : BigDecimal.ZERO);
        }

        response.setSnapshotDate(new Date());

        // 4. Lưu Cache
        cacheService.putProductionDashboard(
                request.getPlantId(), request.getPeriodType(), request.getPeriodValue(), response);

        return response;
    }
}