package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Cache.serviceCache.AttendanceCacheService;
import com.atlas_erp.hr_service.Dto.AttendanceItemDTO;
import com.atlas_erp.hr_service.Dto.Request.Attendance.*;
import com.atlas_erp.hr_service.Dto.Response.Attendance.*;
import com.atlas_erp.hr_service.Entity.AttendanceEntity;
import com.atlas_erp.hr_service.Event.dtoEvent.AttendanceAdjustmentRequestedEvent;
import com.atlas_erp.hr_service.Event.dtoEvent.AttendanceImportedEvent;
import com.atlas_erp.hr_service.Event.dtoEvent.AttendanceLockedEvent;
import com.atlas_erp.hr_service.Event.serviceEvent.AttendanceEventService;
import com.atlas_erp.hr_service.Repository.AttendanceRepository;
import com.atlas_erp.hr_service.Repository.EmployeeAssignmentRepository;
import com.atlas_erp.hr_service.Repository.EmployeeRepository;
import com.atlas_erp.hr_service.Service.AttendanceService;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.Map;
import com.atlas_erp.hr_service.enums.AttendanceStatus;
import com.atlas_erp.hr_service.enums.EmploymentStatus;
import com.atlas_erp.hr_service.enums.LeaveType;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceCacheService attendanceCacheService;
    private final AttendanceEventService attendanceEventService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeAssignmentRepository assignmentRepository;

    @Override
    public AttendanceListResponse getAttendance(AttendanceListRequest request) {
        // 1. Tạo Key và kiểm tra Cache
        String key = attendanceCacheService.generateKey(request);
        AttendanceListResponse cached = attendanceCacheService.getAttendanceList(key);
        if (cached != null)
            return cached;

        // 2. Xây dựng bộ lọc động
        Specification<AttendanceEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo khoảng thời gian (Bắt buộc)
            if (request.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("workDate"), request.getFromDate()));
            }
            if (request.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("workDate"), request.getToDate()));
            }

            // Lọc theo nhân viên cụ thể
            if (StringUtils.hasText(request.getEmployeeId())) {
                predicates.add(cb.equal(root.get("employeeId"), UUID.fromString(request.getEmployeeId())));
            }

            // Lọc theo phòng ban (Lưu ý: AttendanceEntity của bạn chỉ có employeeId,
            // nên ta cần Join sang bảng Assignment để lọc theo phòng ban)
            if (StringUtils.hasText(request.getDepartmentId())) {
                // Logic này giả định bạn muốn lọc những người thuộc phòng ban đó tại thời điểm
                // chấm công
                // Để đơn giản và hiệu năng cao, ta có thể query danh sách employeeId thuộc
                // phòng ban trước
                // hoặc dùng Subquery/Join nếu cần chính xác tuyệt đối.
            }

            query.orderBy(cb.desc(root.get("workDate")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 3. Truy vấn DB
        List<AttendanceEntity> entities = attendanceRepository.findAll(spec);

        // 4. Map sang DTO
        List<AttendanceItemDTO> dtoList = entities.stream().map(entity -> {
            AttendanceItemDTO dto = new AttendanceItemDTO();
            dto.setEmployeeId(entity.getEmployeeId().toString());
            dto.setWorkDate(entity.getWorkDate());
            dto.setCheckIn(entity.getCheckIn());
            dto.setCheckOut(entity.getCheckOut());
            dto.setWorkingHours(entity.getWorkedHours());
            // Trạng thái có thể tính toán dựa trên checkIn/checkOut hoặc field locker
            dto.setStatus(entity.getLocked() ? "LOCKED" : "OPEN");
            return dto;
        }).collect(Collectors.toList());

        AttendanceListResponse response = new AttendanceListResponse();
        response.setList(dtoList);

        // 5. Lưu vào Cache
        attendanceCacheService.putAttendanceList(key, response);

        return response;
    }

    @Override
    @Transactional
    public AttendanceImportResponse importAttendance(AttendanceImportRequest request) {
        // // 1. Kiểm tra đầu vào
        // if (request.getFile() == null || request.getFile().isEmpty()) {
        // throw new IllegalArgumentException("File import không hợp lệ hoặc rỗng.");
        // }

        // List<AttendanceEntity> toSaveList = new ArrayList<>();
        // List<UUID> affectedEmployeeIds = new ArrayList<>();
        // int successCount = 0;
        // int errorCount = 0;

        // try {
        // // --- GIẢ LẬP QUY TRÌNH PARSE FILE ---
        // // Trong thực tế, bạn sẽ dùng Apache POI hoặc OpenCSV để loop qua các dòng ở
        // đây
        // // List<Row> rows = excelService.read(request.getFile());

        // for (int i = 0; i < 20; i++) { // Giả lập loop qua 20 dòng dữ liệu
        // try {
        // // Giả lập dữ liệu từ file
        // UUID mockEmpId = UUID.randomUUID();
        // LocalDate mockDate = LocalDate.now().minusDays(1);

        // // 2. NGHIỆP VỤ: Kiểm tra ngày đã chốt công (Locked) chưa
        // boolean isLocked =
        // attendanceRepository.existsByWorkDateAndLockedTrue(mockDate);
        // if (isLocked) {
        // log.warn("Ngày {} đã bị khóa công, bỏ qua dòng {}", mockDate, i);
        // errorCount++;
        // continue;
        // }

        // // 3. NGHIỆP VỤ: Tạo thực thể và tính toán giờ công
        // AttendanceEntity entity = new AttendanceEntity();
        // entity.setEmployeeId(mockEmpId);
        // entity.setWorkDate(mockDate);
        // entity.setCheckIn(mockDate.atTime(8, 0)); // 08:00 AM
        // entity.setCheckOut(mockDate.atTime(17, 30)); // 05:30 PM

        // // Tính giờ công: (17.5 - 8) - 1.5 (nghỉ trưa) = 8.0 giờ
        // entity.setWorkedHours(new BigDecimal("8.00"));
        // entity.setSource("IMPORT_EXCEL");
        // entity.setLocked(false);

        // toSaveList.add(entity);
        // affectedEmployeeIds.add(mockEmpId);
        // successCount++;

        // // 4. TỐI ƯU: Batch Save nếu danh sách quá lớn
        // if (toSaveList.size() >= 500) {
        // attendanceRepository.saveAll(toSaveList);
        // toSaveList.clear();
        // }
        // } catch (Exception rowEx) {
        // errorCount++;
        // }
        // }

        // // Lưu những bản ghi cuối cùng
        // if (!toSaveList.isEmpty()) {
        // attendanceRepository.saveAll(toSaveList);
        // }

        // // 5. SAU KHI LƯU THÀNH CÔNG: Dọn dẹp Cache và Báo hiệu cho Service khác
        // if (successCount > 0) {
        // // Xóa cache chấm công vì dữ liệu đã thay đổi
        // attendanceCacheService.evictAttendanceCache();

        // // Phát Event để Payroll Service tính lại lương cho các nhân viên này
        // AttendanceImportedEvent event = AttendanceImportedEvent.builder()
        // .importTime(Instant.now())
        // .totalRecords(successCount)
        // .records(affectedEmployeeIds.stream().distinct().collect(Collectors.toList()))
        // .build();

        // attendanceEventService.publishAttendanceImportedEvent(event);
        // }

        // } catch (Exception e) {
        // log.error("Lỗi hệ thống khi import: {}", e.getMessage());
        // return new AttendanceImportResponse(false, 0, 0);
        // }

        // // 6. Trả về kết quả tổng hợp
        // AttendanceImportResponse response = new AttendanceImportResponse();
        // response.setSuccess(true);
        // response.setTotalRecords(successCount + errorCount);
        // response.setErrorRecords(errorCount);

        // return response;
        return null;
    }

    @Override
    @Transactional
    public AttendanceLockResponse lockAttendance(AttendanceLockRequest request) {
        // 1. Validate thời gian
        if (request.getMonth() < 1 || request.getMonth() > 12) {
            throw new IllegalArgumentException("Tháng không hợp lệ");
        }

        // 2. Xác định khoảng thời gian đầu tháng và cuối tháng
        LocalDate startDate = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 3. Nghiệp vụ: Cập nhật trạng thái Locked cho toàn bộ Attendance trong khoảng
        // này
        // Ta sử dụng custom query trong repository hoặc query rồi saveAll
        List<AttendanceEntity> attendances = attendanceRepository.findByWorkDateBetween(startDate, endDate);

        if (attendances.isEmpty()) {
            log.warn("Không có dữ liệu chấm công để khóa trong kỳ {}/{}", request.getMonth(), request.getYear());
        } else {
            attendances.forEach(a -> a.setLocked(true));
            attendanceRepository.saveAll(attendances);
        }

        // 4. Xóa cache (vì trạng thái dữ liệu đã thay đổi từ OPEN sang LOCKED)
        attendanceCacheService.evictAttendanceCache();

        // 5. Phát Event thông báo cho Payroll Service
        AttendanceLockedEvent event = AttendanceLockedEvent.builder()
                .month(request.getMonth())
                .year(request.getYear())
                .lockDate(LocalDate.now())
                .status("LOCKED")
                .build();

        attendanceEventService.publishAttendanceLockedEvent(event);

        // 6. Trả về response
        AttendanceLockResponse response = new AttendanceLockResponse();
        response.setEvent("ATTENDANCE_LOCKED");
        response.setSuccess(true);

        log.info("Đã khóa công kỳ {}/{}", request.getMonth(), request.getYear());
        return response;
    }

    @Override
    @Transactional
    public AttendanceAdjustResponse adjustAttendance(AttendanceAdjustRequest request) {
        // 1. Kiểm tra bản ghi chấm công có tồn tại và đã bị khóa chưa
        boolean isLocked = attendanceRepository.existsByWorkDateAndLockedTrue(request.getDate());
        if (isLocked) {
            throw new IllegalStateException("Không thể điều chỉnh vì kỳ công này đã bị khóa.");
        }

        // 2. Tạo bản ghi lưu vết yêu cầu điều chỉnh (Lưu vào DB nội bộ để Audit)
        // Giả sử bạn có bảng AttendanceAdjustmentEntity
        String adjustmentId = UUID.randomUUID().toString();
        log.info("Khởi tạo yêu cầu điều chỉnh chấm công cho nhân viên: {}", request.getEmployeeId());

        // 3. Gửi Event sang Workflow Service qua Kafka
        // Workflow Service sẽ nhận bản tin này và tạo một Instance (tiến trình) phê
        // duyệt
        AttendanceAdjustmentRequestedEvent event = AttendanceAdjustmentRequestedEvent.builder()
                .adjustmentId(adjustmentId)
                .employeeId(UUID.fromString(request.getEmployeeId()))
                .date(request.getDate())
                .adjustmentType(request.getAdjustmentType())
                .oldValue(request.getOldValue())
                .newValue(request.getNewValue())
                .reason(request.getReason())
                .requestedBy("CURRENT_USER_ID") // Lấy từ SecurityContext
                .build();

        attendanceEventService.publishAdjustmentRequestEvent(event);

        // 4. Trả về thông tin để Frontend theo dõi
        AttendanceAdjustResponse response = new AttendanceAdjustResponse();
        response.setWorkflowInstanceId("WF-" + adjustmentId); // Thường Workflow Service sẽ trả về ID thật
        response.setStatus("PENDING_APPROVAL");

        return response;
    }

    @Override
    public AttendanceSummaryResponse getSummary(AttendanceSummaryRequest request) {
        // 1. Kiểm tra Cache trước để tối ưu hiệu năng
        String cacheKey = attendanceCacheService.generateSummaryKey(request);
        AttendanceSummaryResponse cached = attendanceCacheService.getSummary(cacheKey);
        if (cached != null) {
            log.info("Lấy dữ liệu tổng hợp từ Cache cho kỳ: {}", request.getPeriod());
            return cached;
        }

        // 2. Phân tích tham số Period (MM-yyyy)
        int month, year;
        try {
            String[] parts = request.getPeriod().split("-");
            month = Integer.parseInt(parts[0]);
            year = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Định dạng kỳ (period) không hợp lệ. Vui lòng dùng MM-yyyy");
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 3. Xác định danh sách nhân viên cần tổng hợp (Lọc theo Dept/Team nếu có)
        List<UUID> targetEmployeeIds = null;
        if (StringUtils.hasText(request.getDepartmentId())) {
            // Giả sử bạn có phương thức lấy list ID nhân viên từ Organization/Assignment
            // Service
            // targetEmployeeIds =
            // assignmentRepository.findEmployeeIdsByDept(UUID.fromString(request.getDepartmentId()));
        }

        // 4. Truy vấn tổng hợp từ Database (Sử dụng các hàm Aggregation của Repository)
        // Tổng số nhân viên thực tế trong đơn vị
        long totalEmployees = (targetEmployeeIds != null) ? targetEmployeeIds.size() : employeeRepository.count();

        // Tổng số ngày công thực tế (Mỗi bản ghi AttendanceEntity = 1 ngày công)
        Integer totalWorkingDays = attendanceRepository.countWorkingDays(startDate, endDate, targetEmployeeIds);

        // Tổng số giờ tăng ca (Overtime)
        Double totalOt = attendanceRepository.sumOvertimeHoursInRange(startDate, endDate, targetEmployeeIds);

        // 5. Tính toán số ngày vắng mặt (Absent)
        // Nghiệp vụ: Lấy số ngày làm việc tiêu chuẩn (vùng này thường là 26 ngày) nhân
        // với tổng nhân viên
        int standardWorkDays = 26;
        int totalPotentialDays = (int) (totalEmployees * standardWorkDays);
        int totalAbsentDays = Math.max(0, totalPotentialDays - (totalWorkingDays != null ? totalWorkingDays : 0));

        // 6. Xây dựng Response
        AttendanceSummaryResponse response = new AttendanceSummaryResponse();
        response.setPeriod(request.getPeriod());
        response.setTotalEmployees((int) totalEmployees);
        response.setTotalWorkingDays(totalWorkingDays != null ? totalWorkingDays : 0);
        response.setTotalOvertimeHours(
                BigDecimal.valueOf(totalOt != null ? totalOt : 0).setScale(2, RoundingMode.HALF_UP));
        response.setTotalAbsentDays(totalAbsentDays);

        // 7. Lưu vào Cache cho các lần truy vấn sau
        attendanceCacheService.putSummary(cacheKey, response);

        return response;
    }

    @Override
    public List<com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetSummaryResponse> getTimesheetSummaries(
            int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Load attendances in period and group by employee
        List<AttendanceEntity> records = attendanceRepository.findByWorkDateBetween(startDate, endDate);
        Map<UUID, List<AttendanceEntity>> byEmp = records.stream()
                .collect(Collectors.groupingBy(AttendanceEntity::getEmployeeId));

        // Load all employees (include non-active as requested)
        List<com.atlas_erp.hr_service.Entity.EmployeeEntity> allEmployees = employeeRepository.findAll();
        Map<UUID, com.atlas_erp.hr_service.Entity.EmployeeEntity> empMap = allEmployees.stream()
                .collect(Collectors.toMap(com.atlas_erp.hr_service.Entity.EmployeeEntity::getId, e -> e));

        List<com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetSummaryResponse> result = new ArrayList<>();

        // compute number of standard working days (weekdays)
        int standardDays = 0;
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY)
                standardDays++;
        }

        // Load assignments for employees in batch (avoid N+1) and pick latest when no
        // current
        List<UUID> empIds = new ArrayList<>(empMap.keySet());
        Map<UUID, com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity> assignMap = new java.util.HashMap<>();
        if (!empIds.isEmpty()) {
            List<com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity> assigns = assignmentRepository
                    .findByEmployeeIdIn(empIds);

            Map<UUID, List<com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity>> byEmpAssign = assigns.stream()
                    .collect(Collectors
                            .groupingBy(com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity::getEmployeeId));

            for (UUID id : empIds) {
                List<com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity> list = byEmpAssign.get(id);
                if (list == null || list.isEmpty())
                    continue;

                // Choose assignment: prefer assignment with null toDate (current), otherwise
                // the one
                // with the latest toDate (most recent historical assignment).
                com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity chosen = list.stream()
                        .max(Comparator.comparing(a -> a.getToDate(),
                                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                        .orElse(null);

                if (chosen != null)
                    assignMap.put(id, chosen);
            }
        }

        // Iterate employees (include those without attendance)
        for (Map.Entry<UUID, com.atlas_erp.hr_service.Entity.EmployeeEntity> empEntry : empMap.entrySet()) {
            UUID empId = empEntry.getKey();
            com.atlas_erp.hr_service.Entity.EmployeeEntity emp = empEntry.getValue();
            List<AttendanceEntity> empRecords = byEmp.getOrDefault(empId, List.of());

            com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetSummaryResponse s = new com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetSummaryResponse();
            s.setEmployeeId(empId);
            s.setEmployeeCode(emp.getEmployeeCode());
            s.setFullName(emp.getFullName());
            s.setAvatarUrl(emp.getAvatarUrl());

            // populate department info from assignment if available
            com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity asg = assignMap.get(empId);
            if (asg != null) {
                s.setDepartmentId(asg.getDepartmentId());
                s.setDepartmentName(asg.getDepartmentName());
            }

            s.setPeriod(String.format("%02d-%d", month, year));
            s.setStandardDays(standardDays);

            // actualDays: count distinct dates where status == WORKING
            int actualDays = (int) empRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.WORKING)
                    .map(AttendanceEntity::getWorkDate)
                    .distinct()
                    .count();
            s.setActualDays(actualDays);

            int paidLeave = (int) empRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.LEAVE
                            && r.getLeaveType() == LeaveType.PAID)
                    .count();
            s.setPaidLeaveDays(paidLeave);

            int unpaidLeave = (int) empRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.LEAVE
                            && (r.getLeaveType() == null || r.getLeaveType() == LeaveType.UNPAID))
                    .count();
            s.setUnpaidLeaveDays(unpaidLeave);

            double otHours = empRecords.stream().mapToDouble(r -> {
                if (r.getOtMinutes() != null)
                    return r.getOtMinutes() / 60.0;
                return 0.0;
            }).sum();
            s.setOtHours(otHours);

            int late = empRecords.stream().mapToInt(r -> r.getLateMinutes() != null ? r.getLateMinutes() : 0).sum();
            s.setLateMinutes(late);

            boolean locked = !empRecords.isEmpty()
                    && empRecords.stream().allMatch(r -> r.getLocked() != null && r.getLocked());
            s.setIsLocked(locked);

            result.add(s);
        }

        return result;
    }

    @Override
    public com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetDetailResponse getTimesheetDetail(UUID employeeId,
            int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<AttendanceEntity> records = attendanceRepository.findByEmployeeIdAndWorkDateBetween(employeeId, startDate,
                endDate);

        // sort records by date for consistent frontend display
        records.sort(Comparator.comparing(AttendanceEntity::getWorkDate));

        com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetDetailResponse detail = new com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetDetailResponse();
        detail.setEmployeeId(employeeId);
        detail.setPeriod(String.format("%02d-%d", month, year));

        List<com.atlas_erp.hr_service.Dto.Response.Timesheet.AttendanceRecordResponse> recs = records.stream()
                .map(r -> {
                    com.atlas_erp.hr_service.Dto.Response.Timesheet.AttendanceRecordResponse ar = new com.atlas_erp.hr_service.Dto.Response.Timesheet.AttendanceRecordResponse();
                    ar.setId(r.getId());
                    ar.setEmployeeId(r.getEmployeeId());
                    ar.setDate(r.getWorkDate());
                    ar.setShiftId(r.getShiftId());
                    ar.setCheckIn(r.getCheckIn());
                    ar.setCheckOut(r.getCheckOut());
                    // Use migrated minute fields only (no fallback to old BigDecimal fields)
                    ar.setWorkMinutes(r.getWorkMinutes() != null ? r.getWorkMinutes() : 0);
                    ar.setOtMinutes(r.getOtMinutes() != null ? r.getOtMinutes() : 0);
                    ar.setLateMinutes(r.getLateMinutes());
                    ar.setEarlyLeaveMinutes(r.getEarlyLeaveMinutes());
                    ar.setStatus(r.getStatus());
                    ar.setLeaveType(r.getLeaveType() != null ? r.getLeaveType().name() : null);
                    ar.setNote(r.getNote());
                    return ar;
                }).collect(Collectors.toList());

        detail.setRecords(recs);
        return detail;
    }

}
