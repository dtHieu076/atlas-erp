package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Cache.serviceCache.ReportCacheService;
import com.atlas_erp.reporting_service.Client.HrClient;
import com.atlas_erp.reporting_service.Client.OrganizationClient;
import com.atlas_erp.reporting_service.Dto.External.EmployeeStatisticsRequest;
import com.atlas_erp.reporting_service.Dto.External.EmployeeStatisticsResponse;
import com.atlas_erp.reporting_service.Dto.Request.HRDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.HRDashboardResponse;
import com.atlas_erp.reporting_service.Service.HRDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HRDashboardServiceImpl implements HRDashboardService {

    private final ReportCacheService cacheService;
    private final OrganizationClient organizationClient;
    private final HrClient hrClient;

    @Override
    public HRDashboardResponse getHRDashboard(HRDashboardRequest request) {
        // 1. Kiểm tra Cache
        HRDashboardResponse cached = cacheService.getHRDashboard(request.getPeriod(), request.getDepartmentId());
        if (cached != null) {
            log.info("🚀 HR Dashboard Cache Hit");
            return cached;
        }

        // 2. Validate phòng ban
        if (request.getDepartmentId() != null) {
            try {
                organizationClient.getDepartmentDetail(request.getDepartmentId());
            } catch (Exception e) {
                log.error("❌ Department not found: {}", request.getDepartmentId());
                throw new RuntimeException("Phòng ban không tồn tại");
            }
        }

        // 3. Gọi HR Service lấy số liệu tổng hợp
        // PHẢI tạo Object Request để khớp với @SpringQueryMap trong HrClient
        EmployeeStatisticsRequest externalRequest = new EmployeeStatisticsRequest();
        externalRequest.setPeriod(request.getPeriod());
        externalRequest.setDepartmentId(request.getDepartmentId());

        // Gọi đúng tên method đã khai báo trong HrClient
        EmployeeStatisticsResponse hrData = hrClient.getEmployeeStatistics(externalRequest);

        // 4. Map dữ liệu vào Response (Kiểm tra null để an toàn)
        HRDashboardResponse response = new HRDashboardResponse();
        response.setPeriod(request.getPeriod());

        if (hrData != null) {
            response.setTotalEmployees(hrData.getTotalEmployees());
            response.setActiveEmployees(hrData.getActiveEmployees());
            response.setNewHires(hrData.getNewHires());
            response.setResignations(hrData.getResignations());
            response.setAttendanceRate(hrData.getAttendanceRate());
            // Lưu ý: doubleValue() nếu totalOvertimeHours là BigDecimal
            response.setOvertimeHours(
                    hrData.getTotalOvertimeHours() != null ? hrData.getTotalOvertimeHours().doubleValue() : 0.0);
        }

        // 5. Lưu Cache và trả về
        cacheService.putHRDashboard(response);

        return response;
    }
}