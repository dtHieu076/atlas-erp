package com.atlas_erp.payroll_service.Service.Impl;

import com.atlas_erp.payroll_service.Repository.PayrollPeriodRepository;
import com.atlas_erp.payroll_service.Service.PayrollPeriodService;
import com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod.*;
import com.atlas_erp.payroll_service.Entity.PayrollPeriodEntity;
import com.atlas_erp.payroll_service.Event.dtoEvent.PayrollPeriodLockedEvent;
import com.atlas_erp.payroll_service.Event.serviceEvent.PayrollPeriodEventService;
import com.atlas_erp.payroll_service.Dto.PayrollPeriodItemDTO;
import com.atlas_erp.payroll_service.Dto.Request.PayrollPeriod.*;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.payroll_service.Cache.serviceCache.PayrollPeriodCacheService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollPeriodServiceImpl implements PayrollPeriodService {

    private final PayrollPeriodRepository payrollPeriodRepository;
    private final PayrollPeriodCacheService payrollPeriodCacheService;
    private final PayrollPeriodEventService eventService;

    @Override
    public PayrollPeriodListResponse list(PayrollPeriodListRequest request) {
        // 1. Chuẩn hóa tham số đầu vào
        int page = (request.getPage() != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;

        // 2. Thử lấy dữ liệu từ Cache
        PayrollPeriodListResponse cachedResponse = payrollPeriodCacheService.getListCache(page, size);
        if (cachedResponse != null) {
            log.info("Lấy danh sách kỳ lương từ Cache - Page: {}, Size: {}", page, size);
            return cachedResponse;
        }

        log.info("Cache miss. Truy vấn Database danh sách kỳ lương...");

        // 3. Truy vấn Database (Sắp xếp theo startDate giảm dần)
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        Page<PayrollPeriodEntity> periodPage = payrollPeriodRepository.findAll(pageable);

        // 4. Map Entity sang DTO
        List<PayrollPeriodItemDTO> dtoList = periodPage.getContent().stream().map(entity -> {
            PayrollPeriodItemDTO dto = new PayrollPeriodItemDTO();
            dto.setId(entity.getId().toString());
            dto.setMonth(entity.getStartDate().getMonthValue());
            dto.setYear(entity.getStartDate().getYear());
            dto.setStartDate(entity.getStartDate());
            dto.setEndDate(entity.getEndDate());
            dto.setStatus(Boolean.TRUE.equals(entity.getLocked()) ? "LOCKED" : "OPEN");
            return dto;
        }).toList();

        // 5. Chuẩn bị Response
        PayrollPeriodListResponse response = new PayrollPeriodListResponse();
        response.setList(dtoList);
        response.setTotal(periodPage.getTotalElements());

        // 6. Lưu kết quả vào Cache cho lần gọi sau
        try {
            payrollPeriodCacheService.putListCache(page, size, response);
        } catch (Exception e) {
            log.warn("Lỗi lưu danh sách vào Cache: {}", e.getMessage());
        }

        return response;
    }

    @Override
    @Transactional
    public CreatePayrollPeriodResponse create(CreatePayrollPeriodRequest request) {
        // 1. Khởi tạo mã kỳ lương (VD: 02-2026)
        String periodCode = String.format("%02d-%d", request.getMonth(), request.getYear());
        log.info("Đang khởi tạo kỳ lương mới: {}", periodCode);

        // 2. Kiểm tra logic ngày tháng
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc.");
        }

        // 3. Kiểm tra xem kỳ lương này đã tồn tại trong DB chưa
        // Sử dụng findByPeriodCode mà chúng ta đã định nghĩa trong Repository
        payrollPeriodRepository.findByPeriodCode(periodCode).ifPresent(p -> {
            throw new IllegalStateException("Kỳ lương " + periodCode + " đã được khởi tạo trước đó.");
        });

        // 4. Khởi tạo Entity và lưu vào Database
        PayrollPeriodEntity period = new PayrollPeriodEntity();
        period.setPeriodCode(periodCode);
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setLocked(false); // Mặc định là mở (OPEN) để tính toán

        // Nếu Entity của bạn có createdAt (theo các ví dụ trước)
        // period.setCreatedAt(Instant.now());

        period = payrollPeriodRepository.save(period);

        // 5. Đồng bộ vào Redis Cache
        // Việc này giúp các API 'get' hoặc 'check status' sau đó không cần xuống DB
        try {
            payrollPeriodCacheService.putPeriod(period);
        } catch (Exception e) {
            // Chỉ log lỗi Redis, không rollback transaction vì DB đã lưu thành công
            log.warn("Không thể lưu kỳ lương vào Cache: {}", e.getMessage());
        }

        // 6. Trả về Response
        CreatePayrollPeriodResponse response = new CreatePayrollPeriodResponse();
        response.setId(period.getId().toString());

        log.info("Tạo kỳ lương thành công. ID: {}", period.getId());
        return response;
    }

    @Override
    @Transactional
    public LockPayrollPeriodResponse lock(UUID periodId) {
        // 1. Tìm và validate
        PayrollPeriodEntity period = payrollPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Kỳ lương không tồn tại"));

        if (Boolean.TRUE.equals(period.getLocked())) {
            return LockPayrollPeriodResponse.fail();
        }

        // 2. Cập nhật trạng thái DB
        period.setLocked(true);
        period.setLockedAt(java.time.Instant.now());
        payrollPeriodRepository.save(period);

        // 3. Xử lý Cache
        payrollPeriodCacheService.evict(periodId);
        payrollPeriodCacheService.evictAllListCache();

        // 4. Phát Event qua Kafka (Mới)
        PayrollPeriodLockedEvent event = PayrollPeriodLockedEvent.builder()
                .periodId(period.getId())
                .periodCode(period.getPeriodCode())
                .lockedAt(period.getLockedAt())
                .build();

        eventService.publishPeriodLocked(event);

        return LockPayrollPeriodResponse.success();
    }
}
