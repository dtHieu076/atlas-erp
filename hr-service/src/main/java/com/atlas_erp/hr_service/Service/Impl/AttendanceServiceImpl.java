package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Request.Attendance.*;
import com.atlas_erp.hr_service.Dto.Response.Attendance.*;
// import com.atlas_erp.hr_service.Repository.AttendanceRepository;
import com.atlas_erp.hr_service.Service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    // private final AttendanceRepository attendanceRepository;

    @Override
    public AttendanceListResponse getAttendance(AttendanceListRequest request) {

        AttendanceListResponse response = new AttendanceListResponse();
        response.setList(List.of()); // trả về danh sách rỗng (mẫu)

        return response;
    }

    @Override
    public AttendanceImportResponse importAttendance(AttendanceImportRequest request) {
        // Thực tế parse file excel/csv (stub)

        AttendanceImportResponse response = new AttendanceImportResponse();
        response.setSuccess(true);
        response.setTotalRecords(0);
        response.setErrorRecords(0);

        return response;
    }

    @Override
    public AttendanceLockResponse lockAttendance(AttendanceLockRequest request) {

        AttendanceLockResponse response = new AttendanceLockResponse();
        response.setEvent("ATTENDANCE_LOCKED");
        response.setSuccess(true);

        return response;
    }

    @Override
    public AttendanceAdjustResponse adjustAttendance(AttendanceAdjustRequest request) {
        // Gửi workflow (stub)

        AttendanceAdjustResponse response = new AttendanceAdjustResponse();
        response.setWorkflowInstanceId("workflow-id");
        response.setStatus("PENDING");

        return response;
    }

    @Override
    public AttendanceSummaryResponse getSummary(AttendanceSummaryRequest request) {
        // Stub tổng hợp chấm công theo kỳ (chưa query DB thật)

        AttendanceSummaryResponse response = new AttendanceSummaryResponse();
        response.setPeriod(request.getPeriod());
        response.setTotalEmployees(0);
        response.setTotalWorkingDays(0);
        response.setTotalOvertimeHours(BigDecimal.ZERO);
        response.setTotalAbsentDays(0);

        return response;
    }

}
