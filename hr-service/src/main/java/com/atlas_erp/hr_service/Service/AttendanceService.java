package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Request.Attendance.*;
import com.atlas_erp.hr_service.Dto.Response.Attendance.*;
import com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetSummaryResponse;
import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    AttendanceListResponse getAttendance(AttendanceListRequest request);

    AttendanceImportResponse importAttendance(AttendanceImportRequest request);

    AttendanceLockResponse lockAttendance(AttendanceLockRequest request);

    AttendanceAdjustResponse adjustAttendance(AttendanceAdjustRequest request);

    AttendanceSummaryResponse getSummary(AttendanceSummaryRequest request);

    List<TimesheetSummaryResponse> getTimesheetSummaries(int month, int year);

    TimesheetDetailResponse getTimesheetDetail(UUID employeeId, int month, int year);
}
