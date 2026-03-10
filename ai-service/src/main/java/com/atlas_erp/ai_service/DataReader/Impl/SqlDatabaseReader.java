package com.atlas_erp.ai_service.DataReader.Impl;

import com.atlas_erp.ai_service.DataReader.DatabaseReader;
import com.atlas_erp.ai_service.Repository.AttendanceRepository;
import com.atlas_erp.ai_service.Repository.EmployeeRepository;
import com.atlas_erp.ai_service.enums.AttendanceStatus;
import com.atlas_erp.ai_service.enums.EmploymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * SQL Database reader implementation.
 * Retrieves data from HR PostgreSQL database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlDatabaseReader implements DatabaseReader {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public long getEmployeeCountByStatus(String status) {
        try {
            EmploymentStatus employmentStatus = EmploymentStatus.valueOf(status.toUpperCase());
            return employeeRepository.countByStatus(employmentStatus);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employment status: {}", status);
            return 0;
        }
    }

    @Override
    public long getActiveEmployeeCount() {
        return employeeRepository.countActiveEmployees();
    }

    @Override
    public long getAttendanceCountByDateAndStatus(String date, String status) {
        try {
            LocalDate workDate = LocalDate.parse(date);
            AttendanceStatus attendanceStatus = AttendanceStatus.valueOf(status.toUpperCase());
            return attendanceRepository.countByDateAndStatus(workDate, attendanceStatus);
        } catch (Exception e) {
            log.warn("Invalid date or attendance status: date={}, status={}", date, status);
            return 0;
        }
    }

    @Override
    public String getTodayAttendanceSummary() {
        LocalDate today = LocalDate.now();
        var attendances = attendanceRepository.findByWorkDate(today);

        long working = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.WORKING)
                .count();
        long absent = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();
        long leave = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LEAVE)
                .count();

        return "Today (" + today + "):\n- Working: " + working + "\n- Absent: " + absent + "\n- Leave: " + leave;
    }
}
