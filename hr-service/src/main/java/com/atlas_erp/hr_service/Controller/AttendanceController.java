package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.Attendance.*;
import com.atlas_erp.hr_service.Dto.Response.Attendance.*;
import com.atlas_erp.hr_service.Service.AttendanceService;
import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetSummaryResponse;
import com.atlas_erp.hr_service.Dto.Response.Timesheet.TimesheetDetailResponse;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 6️⃣ GET /attendance
    @GetMapping
    public AttendanceListResponse getAttendance(
            AttendanceListRequest request) {
        return attendanceService.getAttendance(request);
    }

    // 7️⃣ POST /attendance/import
    @PostMapping("/import")
    public AttendanceImportResponse importAttendance(
            @RequestParam("file") MultipartFile file) {
        AttendanceImportRequest request = new AttendanceImportRequest();
        request.setFile(file);
        return attendanceService.importAttendance(request);
    }

    // 8️⃣ POST /attendance/lock
    @PostMapping("/lock")
    public AttendanceLockResponse lockAttendance(
            @RequestBody AttendanceLockRequest request) {
        return attendanceService.lockAttendance(request);
    }

    // 14️⃣ POST /attendance/adjust
    @PostMapping("/adjust")
    public AttendanceAdjustResponse adjustAttendance(
            @RequestBody AttendanceAdjustRequest request) {
        return attendanceService.adjustAttendance(request);
    }

    // 15️⃣ GET /attendance/summary
    @GetMapping("/summary")
    public AttendanceSummaryResponse getSummary(
            AttendanceSummaryRequest request) {
        return attendanceService.getSummary(request);
    }

    // GET /attendance/timesheet/summary?month=&year=&departmentId=
    @GetMapping("/timesheet/summary")
    public ResponseEntity<ApiResponse<java.util.List<TimesheetSummaryResponse>>> getTimesheetSummary(
            @RequestParam int month,
            @RequestParam int year) {

        java.util.List<TimesheetSummaryResponse> list = attendanceService.getTimesheetSummaries(month, year);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // GET /attendance/timesheet/detail?employeeId=&month=&year=
    @GetMapping("/timesheet/detail")
    public ResponseEntity<ApiResponse<TimesheetDetailResponse>> getTimesheetDetail(
            @RequestParam String employeeId,
            @RequestParam int month,
            @RequestParam int year) {

        TimesheetDetailResponse detail = attendanceService.getTimesheetDetail(java.util.UUID.fromString(employeeId),
                month, year);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }
}
