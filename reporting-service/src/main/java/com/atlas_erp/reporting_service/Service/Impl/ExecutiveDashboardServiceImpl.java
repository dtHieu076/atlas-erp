package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Cache.serviceCache.ReportCacheService;
import com.atlas_erp.reporting_service.Client.AccountingClient;
import com.atlas_erp.reporting_service.Client.HrClient;
import com.atlas_erp.reporting_service.Client.ProductionClient;
import com.atlas_erp.reporting_service.Dto.External.EmployeeStatisticsRequest;
import com.atlas_erp.reporting_service.Dto.External.EmployeeStatisticsResponse;
import com.atlas_erp.reporting_service.Dto.External.FinancialReportResponse;
import com.atlas_erp.reporting_service.Dto.External.ProductionSummaryResponse;
import com.atlas_erp.reporting_service.Dto.Request.ExecutiveDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.ExecutiveDashboardResponse;
import com.atlas_erp.reporting_service.Service.ExecutiveDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutiveDashboardServiceImpl implements ExecutiveDashboardService {

    private final ReportCacheService cacheService;
    private final AccountingClient accountingClient;
    private final HrClient hrClient;
    private final ProductionClient productionClient;

    @Override
    public ExecutiveDashboardResponse getExecutiveDashboard(ExecutiveDashboardRequest request) {

        // 1. Kiểm tra Cache
        ExecutiveDashboardResponse cachedResponse = cacheService.getExecutiveDashboard(
                request.getPeriodType(), request.getPeriodValue());

        if (cachedResponse != null) {
            log.info("🚀 Dashboard Cache Hit: {}-{}", request.getPeriodType(), request.getPeriodValue());
            return cachedResponse;
        }

        log.info("🐢 Dashboard Cache Miss: Fetching data from multiple services...");

        // 2. Gọi Accounting Service (Lấy Revenue, Expense, Profit)
        // Lưu ý: Đảm bảo FinancialReportResponse đã có getData() trả về Map
        FinancialReportResponse financialData = accountingClient.getFinancialData(request.getPeriodValue(), "PNL");
        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        if (financialData != null && financialData.getData() != null) {
            revenue = financialData.getData().getOrDefault("TOTAL_REVENUE", BigDecimal.ZERO);
            expense = financialData.getData().getOrDefault("TOTAL_EXPENSE", BigDecimal.ZERO);
        }
        BigDecimal profit = revenue.subtract(expense);

        // 3. Gọi HR Service - SỬA LỖI TẠI ĐÂY
        // Thay vì gọi getAttendanceSummary thiếu param, ta dùng API thống kê tổng hợp
        EmployeeStatisticsRequest hrRequest = new EmployeeStatisticsRequest();
        hrRequest.setPeriod(request.getPeriodValue());
        hrRequest.setDepartmentId(null); // Dashboard Ban Giám Đốc lấy toàn công ty

        EmployeeStatisticsResponse hrStats = hrClient.getEmployeeStatistics(hrRequest);
        Integer totalEmployee = (hrStats != null) ? hrStats.getTotalEmployees() : 0;

        // 4. Gọi Production Service
        ProductionSummaryResponse prodSummary = productionClient.getProductionSummary(request.getPeriodValue());
        BigDecimal totalActualProduction = (prodSummary != null && prodSummary.getTotalActualProduction() != null)
                ? prodSummary.getTotalActualProduction()
                : BigDecimal.ZERO;

        // 5. Tính toán Productivity Index (Sản lượng / Nhân sự)
        BigDecimal productivityIndex = BigDecimal.ZERO;
        if (totalEmployee != null && totalEmployee > 0) {
            productivityIndex = totalActualProduction.divide(
                    BigDecimal.valueOf(totalEmployee), 2, RoundingMode.HALF_UP);
        }

        // 6. Build Response
        ExecutiveDashboardResponse response = new ExecutiveDashboardResponse();
        response.setRevenue(revenue);
        response.setExpense(expense);
        response.setProfit(profit);
        response.setTotalEmployee(totalEmployee);
        response.setProductivityIndex(productivityIndex);
        response.setSnapshotDate(new Date());

        // 7. Lưu Cache
        cacheService.putExecutiveDashboard(request.getPeriodType(), request.getPeriodValue(), response);

        return response;
    }
}