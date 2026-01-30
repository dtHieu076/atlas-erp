package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Request.Attendance.*;
import com.atlas_erp.hr_service.Dto.Response.Attendance.*;

public interface AttendanceService {

    AttendanceListResponse getAttendance(AttendanceListRequest request);

    AttendanceImportResponse importAttendance(AttendanceImportRequest request);

    AttendanceLockResponse lockAttendance(AttendanceLockRequest request);

    AttendanceAdjustResponse adjustAttendance(AttendanceAdjustRequest request);

    AttendanceSummaryResponse getSummary(AttendanceSummaryRequest request);
}

