package com.atlas_erp.ai_service.tools;

import com.atlas_erp.ai_service.Repository.AttendanceRepository;
import com.atlas_erp.ai_service.Repository.EmployeeRepository;
import com.atlas_erp.ai_service.Entity.AttendanceEntity;
import com.atlas_erp.ai_service.Entity.EmployeeEntity;
import com.atlas_erp.ai_service.enums.EmploymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HrTools {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final DataSource dataSource;

    // Internal Records for Service logic
    public record SqlQueryRequest(String sql) {
    }

    public record SearchRequest(String name) {
    }

    public record LimitRequest(int limit) {
    }

    public record DateRequest(String date) {
    }

    public record GenderRatioRequest(String departmentName) {
    }

    public record IdRequest(UUID id) {
    }

    public record DaysRequest(int days) {
    }

    public String queryHrDatabase(SqlQueryRequest request) {
        String sql = request.sql();
        log.info("Executing HR query: {}", sql);

        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("SELECT") || isDangerous(upperSql)) {
            return "Error: Chỉ chấp nhận câu lệnh SELECT an toàn.";
        }

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            int columnCount = rs.getMetaData().getColumnCount();
            List<Map<String, Object>> results = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
            return toJson(results);
        } catch (Exception e) {
            return "Error executing query: " + e.getMessage();
        }
    }

    public String searchEmployees(SearchRequest request) {
        log.info("Searching employees with name: {}", request.name());
        try {
            List<EmployeeEntity> employees = employeeRepository.findByFullNameContainingIgnoreCase(request.name());
            return toJson(employees.stream().limit(50).map(this::mapEmployeeToMap).toList());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getTodayAttendance() {
        return getAttendanceByDate(new DateRequest(LocalDate.now().toString()));
    }

    public String getEmployeeList(LimitRequest request) {
        int actualLimit = Math.min(request.limit() > 0 ? request.limit() : 20, 100);
        try {
            return toJson(
                    employeeRepository.findAll().stream().limit(actualLimit).map(this::mapEmployeeToMap).toList());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getEmployeeStatistics() {
        try {
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("total_employees", employeeRepository.count());
            stats.put("active", employeeRepository.countByStatus(EmploymentStatus.ACTIVE));
            stats.put("probation", employeeRepository.countByStatus(EmploymentStatus.PROBATION));
            return toJson(Collections.singletonList(stats));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getAttendanceByDate(DateRequest request) {
        log.info("Getting attendance for date: {}", request.date());
        try {
            LocalDate workDate = LocalDate.parse(request.date());
            List<AttendanceEntity> attendances = attendanceRepository.findByWorkDate(workDate);
            return toJson(processAttendanceResults(attendances));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getGenderRatio(GenderRatioRequest request) {
        log.info("Calculating gender ratio");
        String sql = "SELECT gender, COUNT(*) as count, ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) as percentage "
                +
                "FROM employees WHERE status = 'ACTIVE' GROUP BY gender";
        return queryHrDatabase(new SqlQueryRequest(sql));
    }

    public String getUpcomingBirthdays() {
        int currentMonth = LocalDate.now().getMonthValue();
        String sql = "SELECT full_name, dob, employee_code FROM employees " +
                "WHERE EXTRACT(MONTH FROM dob) = " + currentMonth + " ORDER BY EXTRACT(DAY FROM dob)";
        return queryHrDatabase(new SqlQueryRequest(sql));
    }

    public String getEmployeeDetail(IdRequest request) {
        return employeeRepository.findById(request.id())
                .map(emp -> toJson(Collections.singletonList(mapEmployeeToMap(emp))))
                .orElse("Error: Không tìm thấy nhân viên.");
    }

    public String getNewHires(DaysRequest request) {
        int days = request.days() > 0 ? request.days() : 30;
        LocalDate startDate = LocalDate.now().minusDays(days);
        try {
            List<EmployeeEntity> employees = employeeRepository.findByHireDateAfter(startDate);
            return toJson(employees.stream().map(this::mapEmployeeToMap).toList());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getEmployeesOnLeave() {
        String sql = "SELECT e.full_name, l.leave_type, l.start_date, l.end_date " +
                "FROM leave_requests l JOIN employees e ON l.employee_id = e.id " +
                "WHERE CURRENT_DATE BETWEEN l.start_date AND l.end_date AND l.status = 'APPROVED'";
        return queryHrDatabase(new SqlQueryRequest(sql));
    }

    // --- Helpers ---

    private boolean isDangerous(String sql) {
        String s = sql.toUpperCase();
        return s.contains("DROP") || s.contains("DELETE") || s.contains("UPDATE") || s.contains("INSERT")
                || s.contains("TRUNCATE");
    }

    private List<Map<String, Object>> processAttendanceResults(List<AttendanceEntity> attendances) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (AttendanceEntity att : attendances) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("employee_id", att.getEmployeeId());
            map.put("work_date", att.getWorkDate());
            map.put("check_in", att.getCheckIn());
            map.put("check_out", att.getCheckOut());
            map.put("status", att.getStatus());
            results.add(map);
        }
        return results;
    }

    private Map<String, Object> mapEmployeeToMap(EmployeeEntity emp) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", emp.getId());
        map.put("code", emp.getEmployeeCode());
        map.put("name", emp.getFullName());
        map.put("status", emp.getStatus());
        return map;
    }

    private String toJson(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty())
            return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            sb.append("{");
            int j = 0;
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                sb.append("\"").append(entry.getKey()).append("\":");
                Object v = entry.getValue();
                if (v instanceof Number || v instanceof Boolean)
                    sb.append(v);
                else
                    sb.append("\"").append(v == null ? "null" : v.toString().replace("\"", "\\\"")).append("\"");
                if (++j < row.size())
                    sb.append(",");
            }
            sb.append("}");
            if (i < data.size() - 1)
                sb.append(",");
        }
        return sb.append("]").toString();
    }
}