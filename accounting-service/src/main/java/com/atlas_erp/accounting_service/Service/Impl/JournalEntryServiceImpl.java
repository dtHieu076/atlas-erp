package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Cache.serviceCache.AccountingCacheService;
import com.atlas_erp.accounting_service.Dto.Request.JournalEntry.*;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.*;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.JournalEntryDetailResponse.JournalEntryLine;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.JournalEntryListResponse.JournalEntrySummary;
import com.atlas_erp.accounting_service.Entity.AccountingPeriodEntity;
import com.atlas_erp.accounting_service.Entity.JournalEntryEntity;
import com.atlas_erp.accounting_service.Entity.JournalEntryLineEntity;
import com.atlas_erp.accounting_service.Event.dtoEvent.JournalEntryPostedEvent;
import com.atlas_erp.accounting_service.Event.serviceEvent.AccountingEventService;
import com.atlas_erp.accounting_service.Repository.AccountingPeriodRepository;
import com.atlas_erp.accounting_service.Repository.JournalEntryLineRepository;
import com.atlas_erp.accounting_service.Repository.JournalEntryRepository;
import com.atlas_erp.accounting_service.Service.JournalEntryService;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryLineRepository lineRepository;
    private final AccountingPeriodRepository periodRepository;
    private final AccountingEventService eventService;
    private final AccountingCacheService cacheService;

    @Override
    @Transactional
    public CreateJournalEntryResponse createJournalEntry(CreateJournalEntryRequest request) {
        log.info("Bắt đầu lập bút toán mới: {}", request.getDescription());

        // 1. Validate: Kiểm tra kỳ kế toán
        AccountingPeriodEntity period = periodRepository.findById(UUID.fromString(request.getPeriodId()))
                .orElseThrow(() -> new RuntimeException("Kỳ kế toán không tồn tại."));

        if (Boolean.TRUE.equals(period.getLocked())) {
            throw new IllegalStateException("Kỳ kế toán đã bị khóa, không thể lập thêm bút toán.");
        }

        // 2. Validate: Kiểm tra tính cân đối (Debit = Credit)
        if (request.getLines() == null || request.getLines().size() < 2) {
            throw new IllegalArgumentException("Một bút toán phải có ít nhất 2 dòng hạch toán.");
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (var line : request.getLines()) {
            totalDebit = totalDebit.add(line.getDebit() != null ? line.getDebit() : BigDecimal.ZERO);
            totalCredit = totalCredit.add(line.getCredit() != null ? line.getCredit() : BigDecimal.ZERO);
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException(
                    "Bút toán không cân đối. Tổng Nợ (" + totalDebit + ") khác Tổng Có (" + totalCredit + ")");
        }

        // 3. Tạo Journal Entry (Header)
        JournalEntryEntity entry = new JournalEntryEntity();
        entry.setEntryDate(request.getEntryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        entry.setDescription(request.getDescription());
        entry.setAccountingPeriodId(period.getId());
        entry.setStatus("DRAFT"); // Trạng thái nháp, chưa ghi sổ (Post)

        entry = journalEntryRepository.save(entry);

        // 4. Tạo các Journal Entry Lines (Detail)
        List<JournalEntryLineEntity> lines = new ArrayList<>();
        for (var lineDto : request.getLines()) {
            JournalEntryLineEntity line = new JournalEntryLineEntity();
            line.setJournalEntryId(entry.getId());
            line.setAccountId(UUID.fromString(lineDto.getAccountId()));
            line.setDebit(lineDto.getDebit());
            line.setCredit(lineDto.getCredit());
            // line.setNote(lineDto.getNote()); // Nếu Entity có trường note
            lines.add(line);
        }
        lineRepository.saveAll(lines);

        log.info("Lập bút toán thành công. ID: {}", entry.getId());

        CreateJournalEntryResponse response = new CreateJournalEntryResponse();
        response.setId(entry.getId().toString());
        return response;
    }

    @Override
    @Transactional
    public PostJournalEntryResponse postJournalEntry(String id, PostJournalEntryRequest request) {
        log.info("📢 Bắt đầu ghi sổ bút toán ID: {}", id);

        // 1. Fetch và Validate Journal Entry
        JournalEntryEntity entry = journalEntryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bút toán."));

        if (!"DRAFT".equalsIgnoreCase(entry.getStatus())) {
            throw new IllegalStateException("Chỉ bút toán ở trạng thái DRAFT mới được phép ghi sổ.");
        }

        // 2. Kiểm tra Kỳ kế toán (Internal check)
        AccountingPeriodEntity period = periodRepository.findById(entry.getAccountingPeriodId())
                .orElseThrow(() -> new RuntimeException("Kỳ kế toán liên quan không tồn tại."));

        if (Boolean.TRUE.equals(period.getLocked())) {
            throw new IllegalStateException("Kỳ kế toán của bút toán này đã bị khóa.");
        }

        // 3. Cập nhật trạng thái ghi sổ
        Instant now = Instant.now();
        entry.setStatus("POSTED");
        entry.setPostedAt(now);
        entry.setPostedBy(UUID.fromString(request.getPostedBy()));

        journalEntryRepository.save(entry);

        // 4. Phát Event thông báo (Tùy chọn lấy referenceType từ line đầu tiên nếu cần)
        // Trong thực tế, bạn có thể lấy referenceType từ DB nếu đã lưu ở bước lập bút
        // toán
        JournalEntryPostedEvent event = JournalEntryPostedEvent.builder()
                .entryId(entry.getId())
                .description(entry.getDescription())
                .postedBy(request.getPostedBy())
                .postedAt(now)
                .referenceType("GENERAL") // Có thể logic hóa dựa trên data
                .build();

        eventService.publishJournalPosted(event);

        log.info("✅ Bút toán {} đã được ghi sổ vĩnh viễn.", id);
        return new PostJournalEntryResponse(true);
    }

    @Override // Chưa cần gấp
    public ReverseJournalEntryResponse reverseJournalEntry(String id, ReverseJournalEntryRequest request) {
        ReverseJournalEntryResponse response = new ReverseJournalEntryResponse();
        response.setNewEntryId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public JournalEntryListResponse listJournalEntries(JournalEntryListRequest request) {
        // 1. Kiểm tra Cache trước
        JournalEntryListResponse cached = cacheService.getJournalList(request);
        if (cached != null) {
            log.info("🚀 Cache Hit: Danh sách bút toán");
            return cached;
        }

        log.info("🐢 Cache Miss: Truy vấn và tính toán danh sách bút toán từ DB");

        // 2. Chuyển đổi Date sang LocalDate cho Query
        LocalDate from = request.getFromDate() != null
                ? request.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        LocalDate to = request.getToDate() != null
                ? request.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        UUID accountUuid = (request.getAccountId() != null) ? UUID.fromString(request.getAccountId()) : null;

        int page = request.getPage() != null ? request.getPage() : 0;

        // 3. Thực hiện Query tổng hợp (JOIN + SUM + GROUP BY)
        Page<Object[]> resultPage = journalEntryRepository.findJournalEntrySummary(
                from, to, accountUuid, PageRequest.of(page, 20));

        // 4. Mapping kết quả Object[] sang DTO
        List<JournalEntrySummary> summaryList = resultPage.getContent().stream().map(row -> {
            JournalEntrySummary summary = new JournalEntrySummary();
            summary.setJournalEntryId(row[0].toString());
            summary.setEntryDate(java.sql.Date.valueOf((LocalDate) row[1]));
            summary.setDescription((String) row[2]);
            summary.setStatus((String) row[3]);
            summary.setTotalDebit((BigDecimal) row[4]);
            summary.setTotalCredit((BigDecimal) row[5]);
            return summary;
        }).collect(Collectors.toList());

        JournalEntryListResponse response = new JournalEntryListResponse();
        response.setJournalEntries(summaryList);

        // 5. Lưu vào Cache trước khi trả về
        cacheService.putJournalList(request, response);

        return response;
    }

    @Override
    public JournalEntryDetailResponse getJournalEntryDetail(String id) {
        // 1. Kiểm tra Cache (Cache-Aside pattern)
        JournalEntryDetailResponse cached = cacheService.getJournalDetail(id);
        if (cached != null) {
            log.info("🚀 Cache Hit: Chi tiết bút toán {}", id);
            return cached;
        }

        log.info("🐢 Cache Miss: Truy vấn DB chi tiết bút toán {}", id);

        // 2. Truy vấn Header (Bút toán chính)
        JournalEntryEntity entry = journalEntryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bút toán với ID: " + id));

        // 3. Truy vấn Details (Các dòng Nợ/Có)
        List<JournalEntryLineEntity> lines = lineRepository.findByJournalEntryId(UUID.fromString(id));

        // 4. Mapping dữ liệu sang DTO
        JournalEntryDetailResponse response = new JournalEntryDetailResponse();
        response.setJournalEntryId(entry.getId().toString());

        // Chuyển đổi từ LocalDate (Entity) sang Date (DTO)
        if (entry.getEntryDate() != null) {
            response.setEntryDate(Date.from(entry.getEntryDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        response.setDescription(entry.getDescription());

        // Resolve người tạo (Tạm thời dùng ID)
        response.setCreatedBy(entry.getPostedBy() != null ? entry.getPostedBy().toString() : "SYSTEM");

        // Mapping danh sách các dòng hạch toán bằng Java Stream
        List<JournalEntryLine> lineDtos = lines.stream().map(line -> {
            JournalEntryLine dto = new JournalEntryLine();
            dto.setAccountId(line.getAccountId().toString());
            dto.setDebit(line.getDebit());
            dto.setCredit(line.getCredit());
            return dto;
        }).collect(Collectors.toList());

        response.setLines(lineDtos);

        // 5. Lưu vào Cache trước khi trả về để tối ưu lần gọi sau
        cacheService.putJournalDetail(id, response);

        return response;
    }
}
