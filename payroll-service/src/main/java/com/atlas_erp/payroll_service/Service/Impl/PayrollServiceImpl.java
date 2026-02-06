package com.atlas_erp.payroll_service.Service.Impl;

import com.atlas_erp.payroll_service.Cache.serviceCache.PayrollPeriodCacheService;
import com.atlas_erp.payroll_service.Client.HRServiceClient;
import com.atlas_erp.payroll_service.Dto.PayrollDetailItemDTO;
import com.atlas_erp.payroll_service.Dto.Request.Payroll.*;
import com.atlas_erp.payroll_service.Dto.Response.Payroll.*;
import com.atlas_erp.payroll_service.Dto.Response.getData.EmployeeListResponse;
import com.atlas_erp.payroll_service.Dto.Response.getData.EmployeePayrollDataResponse;
import com.atlas_erp.payroll_service.Entity.PayrollComponentEntity;
import com.atlas_erp.payroll_service.Entity.PayrollEntity;
import com.atlas_erp.payroll_service.Entity.PayrollPeriodEntity;
import com.atlas_erp.payroll_service.Repository.PayrollComponentRepository;
import com.atlas_erp.payroll_service.Repository.PayrollPeriodRepository;
import com.atlas_erp.payroll_service.Repository.PayrollRepository;
import com.atlas_erp.payroll_service.Service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollServiceImpl implements PayrollService {

        private final PayrollRepository payrollRepository;
        private final PayrollPeriodRepository payrollPeriodRepository;
        private final PayrollComponentRepository payrollComponentRepository;
        private final PayrollPeriodCacheService payrollPeriodCacheService;
        private final HRServiceClient hrServiceClient;

        @Override
        @Transactional
        public PayrollCalculateResponse calculate(PayrollCalculateRequest request) {
                UUID pId = UUID.fromString(request.getPeriodId());

                // 1. Kiểm tra kỳ lương và Cache
                PayrollPeriodEntity period = payrollPeriodRepository.findById(pId)
                                .orElseThrow(() -> new ResourceNotFoundException("Kỳ lương không tồn tại"));

                if (Boolean.TRUE.equals(period.getLocked())) {
                        throw new IllegalStateException("Kỳ lương đã khóa, không thể tính lại!");
                }

                // 2. Dọn dẹp dữ liệu cũ nếu tính lại (Tránh nhân đôi dữ liệu)
                payrollRepository.deleteByPayrollPeriodId(pId);

                // 3. Gọi HR Service lấy dữ liệu tổng thể (Bulk Fetch)
                List<EmployeePayrollDataResponse> hrData = hrServiceClient.getPayrollData(
                                period.getStartDate().toString(),
                                period.getEndDate().toString());

                BigDecimal totalPeriodAmount = BigDecimal.ZERO;
                int employeeCount = 0;

                // 4. Vòng lặp tính toán
                for (EmployeePayrollDataResponse data : hrData) {
                        // Công thức: (Lương cơ bản / Ngày tiêu chuẩn) * Ngày làm thực tế + Phụ cấp
                        BigDecimal dailyRate = data.getBaseSalary().divide(
                                        BigDecimal.valueOf(data.getStandardDays()), 2, RoundingMode.HALF_UP);

                        BigDecimal earnings = dailyRate.multiply(BigDecimal.valueOf(data.getActualWorkedDays()));
                        BigDecimal netSalary = earnings.add(data.getAllowances());

                        // 5. Lưu Header lương (PayrollEntity)
                        PayrollEntity payroll = new PayrollEntity();
                        payroll.setEmployeeId(data.getEmployeeId());
                        payroll.setPayrollPeriodId(pId);
                        payroll.setGrossSalary(data.getBaseSalary());
                        payroll.setNetSalary(netSalary);
                        payroll.setStatus("DRAFT");
                        payroll = payrollRepository.save(payroll);

                        // 6. Lưu Chi tiết (PayrollComponentEntity)
                        saveComponent(payroll.getId(), "BASE_EARNING", "EARNING", earnings);
                        if (data.getAllowances().compareTo(BigDecimal.ZERO) > 0) {
                                saveComponent(payroll.getId(), "WOOD_ALLOWANCE", "EARNING", data.getAllowances());
                        }

                        totalPeriodAmount = totalPeriodAmount.add(netSalary);
                        employeeCount++;
                }

                // 7. Cập nhật trạng thái và Cache
                payrollPeriodCacheService.evictAllListCache();

                // 8. Trả về Response
                PayrollCalculateResponse response = new PayrollCalculateResponse();
                response.setPeriodId(request.getPeriodId());
                response.setTotalEmployee(employeeCount);
                response.setTotalAmount(totalPeriodAmount);
                response.setStatus("CALCULATED");
                response.setCalculatedAt(LocalDateTime.now());

                log.info("Finished calculation for period {}: {} employees processed.", period.getPeriodCode(),
                                employeeCount);
                return response;
        }

        private void saveComponent(UUID payrollId, String code, String type, BigDecimal amount) {
                PayrollComponentEntity component = new PayrollComponentEntity();
                component.setPayrollId(payrollId);
                component.setComponentCode(code);
                component.setComponentType(type);
                component.setAmount(amount);
                payrollComponentRepository.save(component);
        }

        @Override
        public PayrollListResponse list(PayrollListRequest request) {
                // 1. Kiểm tra Cache trước (Dựa trên key động chứa các filter)
                PayrollListResponse cached = payrollPeriodCacheService.getPayrollListCache(request);
                if (cached != null) {
                        log.info("Cache Hit - Trả về danh sách bảng lương từ Redis");
                        return cached;
                }

                // 2. Thiết lập phân trang
                int page = (request.getPage() != null && request.getPage() >= 0) ? request.getPage() : 0;
                int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;
                Pageable pageable = PageRequest.of(page, size);
                UUID pId = UUID.fromString(request.getPeriodId());

                // 3. Xử lý logic lọc dữ liệu
                Page<PayrollEntity> payrollPage;
                List<UUID> filteredEmployeeIds = new ArrayList<>();

                // TH1: Nếu lọc theo Department, phải hỏi HR Service danh sách EmployeeId trước
                if (request.getDepartmentId() != null && !request.getDepartmentId().isEmpty()) {
                        EmployeeListResponse hrDeptResponse = hrServiceClient.getEmployees(
                                        request.getDepartmentId(), "ACTIVE", 0, 1000); // Lấy sỉ IDs

                        filteredEmployeeIds = hrDeptResponse.getList().stream()
                                        .map(emp -> UUID.fromString(emp.getId()))
                                        .collect(Collectors.toList());

                        if (filteredEmployeeIds.isEmpty()) {
                                return new PayrollListResponse(Collections.emptyList(), 0L);
                        }

                        payrollPage = payrollRepository.findByPayrollPeriodIdAndEmployeeIdIn(pId, filteredEmployeeIds,
                                        pageable);
                }
                // TH2: Nếu chỉ lọc theo 1 nhân viên cụ thể
                else if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()) {
                        payrollPage = payrollRepository.findByPayrollPeriodIdAndEmployeeIdIn(
                                        pId, List.of(UUID.fromString(request.getEmployeeId())), pageable);
                }
                // TH3: Lấy tất cả theo kỳ lương
                else {
                        payrollPage = payrollRepository.findByPayrollPeriodId(pId, pageable);
                }

                if (payrollPage.isEmpty()) {
                        return new PayrollListResponse(Collections.emptyList(), 0L);
                }

                // 4. Lấy thông tin hiển thị (Name/Code) từ HR Service cho trang hiện tại
                List<String> currentIds = payrollPage.getContent().stream()
                                .map(p -> p.getEmployeeId().toString())
                                .toList();

                log.info("Đang lấy thông tin nhân sự cho các ID: {}", currentIds);
                // Gọi HR Service một lần nữa để lấy thông tin chi tiết của danh sách IDs này
                // (Aggregator Pattern)
                // Ở đây ta tận dụng lại hàm getEmployees nhưng không truyền deptId để lấy thông
                // tin chung
                EmployeeListResponse hrDetailResponse = hrServiceClient.getEmployees(null, null, 0, 1000);
                Map<String, EmployeeListResponse.EmployeeDTO> employeeMap = hrDetailResponse.getList().stream()
                                .collect(Collectors.toMap(EmployeeListResponse.EmployeeDTO::getId, e -> e,
                                                (a, b) -> a));

                // 5. Map và gộp dữ liệu (Merge Data)
                List<PayrollDetailItemDTO> dtoList = payrollPage.getContent().stream().map(entity -> {
                        PayrollDetailItemDTO dto = new PayrollDetailItemDTO();
                        String empId = entity.getEmployeeId().toString();
                        EmployeeListResponse.EmployeeDTO hrInfo = employeeMap.get(empId);

                        dto.setEmployeeId(empId);
                        dto.setEmployeeCode(hrInfo != null ? hrInfo.getEmployeeCode() : "N/A");
                        dto.setFullName(hrInfo != null ? hrInfo.getFullName() : "Unknown");

                        dto.setBaseSalary(entity.getGrossSalary());
                        dto.setNetSalary(entity.getNetSalary());
                        dto.setStatus(entity.getStatus());

                        // Lưu ý: Các trường overtimePay, allowance cần query từ PayrollComponentEntity
                        // nếu cần chi tiết
                        dto.setOvertimePay(BigDecimal.ZERO); // Mặc định hoặc query thêm
                        dto.setAllowance(BigDecimal.ZERO);
                        dto.setDeduction(BigDecimal.ZERO);

                        return dto;
                }).toList();

                // 6. Đóng gói kết quả & Lưu Cache
                PayrollListResponse response = new PayrollListResponse();
                response.setList(dtoList);
                response.setTotal(payrollPage.getTotalElements());

                payrollPeriodCacheService.putPayrollListCache(request, response);

                return response;
        }

        @Override
        public PayrollRecalculateResponse recalculate(PayrollRecalculateRequest request) {

                PayrollRecalculateResponse response = new PayrollRecalculateResponse();

                response.setResult("OK"); // mock result
                response.setRecalculatedCount(
                                request.getPayrollId() != null ? 1 : 0 // mock logic nhẹ
                );
                response.setPeriodId(request.getPeriodId());
                response.setMessage("Recalculated");

                return response;
        }

        @Override
        public ApprovePayrollResponse approve(UUID payrollId, ApprovePayrollRequest request) {

                ApprovePayrollResponse response = new ApprovePayrollResponse();

                response.setSuccess(true); // mock approve OK
                response.setPayrollId(payrollId.toString());
                response.setStatus("APPROVED");
                response.setApprovedAt(LocalDateTime.now());
                response.setApprovedBy("SYSTEM"); // mock user

                return response;
        }

        @Override
        public RejectPayrollResponse reject(UUID payrollId, RejectPayrollRequest request) {

                RejectPayrollResponse response = new RejectPayrollResponse();

                response.setSuccess(true); // mock reject OK
                response.setPayrollId(payrollId.toString());
                response.setStatus("REJECTED");
                response.setRejectedAt(LocalDateTime.now());
                response.setRejectedBy("SYSTEM"); // mock user

                return response;
        }

        @Override
        public PayrollDetailResponse detail(UUID payrollId) {

                PayrollDetailResponse response = new PayrollDetailResponse();

                response.setPayrollId(payrollId.toString());
                response.setEmployeeId("EMP-001"); // mock
                response.setPeriodId("2025-01"); // mock

                response.setBaseSalary(BigDecimal.valueOf(10_000_000));
                response.setOvertimePay(BigDecimal.valueOf(1_000_000));
                response.setBonus(BigDecimal.valueOf(500_000));
                response.setDeductions(BigDecimal.valueOf(300_000));
                response.setNetSalary(BigDecimal.valueOf(11_200_000));

                response.setCurrency("VND");
                response.setStatus("CALCULATED");

                response.setGeneratedAt(LocalDateTime.now().minusDays(1));
                response.setLastUpdatedAt(LocalDateTime.now());

                return response;
        }

}
