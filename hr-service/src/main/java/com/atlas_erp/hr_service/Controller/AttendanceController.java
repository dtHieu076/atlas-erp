package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.Attendance.*;
import com.atlas_erp.hr_service.Dto.Response.Attendance.*;
import com.atlas_erp.hr_service.Service.AttendanceService;
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
}
