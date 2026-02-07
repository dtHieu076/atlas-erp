package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Dto.Request.LockAccountingPeriodRequest;
import com.atlas_erp.accounting_service.Dto.Response.LockAccountingPeriodResponse;
import com.atlas_erp.accounting_service.Entity.AccountingPeriodEntity;
import com.atlas_erp.accounting_service.Event.dtoEvent.AccountingPeriodLockedEvent;
import com.atlas_erp.accounting_service.Event.serviceEvent.AccountingEventService;
import com.atlas_erp.accounting_service.Repository.AccountingPeriodRepository;
import com.atlas_erp.accounting_service.Service.AccountingPeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingPeriodServiceImpl implements AccountingPeriodService {

    private final AccountingPeriodRepository periodRepository;
    private final AccountingEventService eventService;

    @Override
    @Transactional
    public LockAccountingPeriodResponse lockPeriod(String id, LockAccountingPeriodRequest request) {
        log.info("🔐 Thực hiện khóa kỳ kế toán ID: {}", id);

        // 1. Tìm kỳ kế toán từ Database
        AccountingPeriodEntity period = periodRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Kỳ kế toán không tồn tại với ID: " + id));

        // 2. Validate logic
        // Nếu đã locked = true thì không cho phép thao tác tiếp
        if (Boolean.TRUE.equals(period.getLocked())) {
            log.warn("Kỳ kế toán {} đã bị khóa trước đó.", period.getPeriodCode());
            return new LockAccountingPeriodResponse(false);
        }

        // 3. Cập nhật trạng thái khóa sổ
        period.setLocked(true);
        // Lưu ý: Nếu muốn lưu người khóa và ngày khóa, bạn hãy bổ sung
        // private UUID closedBy và private Instant closedAt vào AccountingPeriodEntity

        periodRepository.save(period);

        // 4. Phát event Kafka để các Service khác (Payroll, Inventory, Production)
        // "đóng băng" dữ liệu
        AccountingPeriodLockedEvent event = AccountingPeriodLockedEvent.builder()
                .periodId(id)
                .lockedBy(request.getClosedBy())
                .lockedAt(Instant.now())
                .build();

        eventService.publishPeriodLocked(event);

        log.info("✅ Đã khóa sổ kỳ: {} thành công.", period.getPeriodCode());

        LockAccountingPeriodResponse response = new LockAccountingPeriodResponse();
        response.setSuccess(true);
        return response;
    }
}