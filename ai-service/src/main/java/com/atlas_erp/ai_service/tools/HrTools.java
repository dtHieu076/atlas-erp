package com.atlas_erp.ai_service.tools;

import com.atlas_erp.ai_service.Repository.AttendanceRepository;
import com.atlas_erp.ai_service.Repository.EmployeeRepository;
import com.atlas_erp.ai_service.Entity.AttendanceEntity;
import com.atlas_erp.ai_service.Entity.EmployeeEntity;
import com.atlas_erp.ai_service.enums.EmploymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

/**
 * HR Tools - Cung cấp các tools để truy vấn dữ liệu HR
 * 
 * Nguyên tắc thiết kế:
 * - Tools đại diện cho khả năng, không phải câu truy vấn cụ thể
 * - LLM tự quyết định tool nào được gọi dựa trên câu hỏi
 * - Chỉ cho phép SELECT (read-only) để bảo mật
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HrTools {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final DataSource dataSource;

    /**
     * Tool tổng quát: Thực thi câu truy vấn SQL để lấy dữ liệu HR
     * CHỈ sử dụng câu lệnh SELECT (read-only)
     */
    @Tool(name = "query_hr_database", description = "Thực thi câu truy vấn SQL để lấy dữ liệu HR. " +
            "CHỈ sử dụng câu lệnh SELECT (read-only). " +
            "Không được phép sử dụng INSERT, UPDATE, DELETE, DROP, TRUNCATE. " +
            "Ví dụ: SELECT COUNT(*) FROM employees WHERE status = 'ACTIVE'")
    public String queryHrDatabase(
            @ToolParam(description = "Câu truy vấn SQL (CHỈ SELECT)") String sql) {

        log.info("Executing HR query: {}", sql);

        // Security check: chỉ cho phép SELECT
        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("SELECT")) {
            log.warn("Blocked non-SELECT query: {}", sql);
            return "Error: Chỉ chấp nhận câu lệnh SELECT. Câu lệnh '" + sql + "' không được phép.";
        }

        // Block dangerous operations
        if (upperSql.contains("DROP") || upperSql.contains("DELETE") ||
                upperSql.contains("UPDATE") || upperSql.contains("INSERT") ||
                upperSql.contains("TRUNCATE") || upperSql.contains("ALTER")) {
            log.warn("Blocked dangerous SQL: {}", sql);
            return "Error: Câu lệnh chứa từ khóa cấm. Chỉ chấp nhận SELECT.";
        }

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            int columnCount = rs.getMetaData().getColumnCount();
            List<Map<String, Object>> results = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            log.info("Query returned {} rows", results.size());
            return toJson(results);

        } catch (Exception e) {
            log.error("Error executing query: {}", e.getMessage(), e);
            return "Error executing query: " + e.getMessage();
        }
    }

    /**
     * Tìm kiếm nhân viên theo tên
     */
    @Tool(name = "search_employees", description = "Tìm kiếm nhân viên theo tên. " +
            "Trả về thông tin cơ bản: id, employee_code, full_name, work_email, phone, status, hire_date.")
    public String searchEmployees(
            @ToolParam(description = "Tên nhân viên cần tìm (có thể là từng phần)") String name) {

        log.info("Searching employees with name: {}", name);

        try {
            List<EmployeeEntity> employees = employeeRepository
                    .findByFullNameContainingIgnoreCase(name);

            List<Map<String, Object>> results = new ArrayList<>();
            for (EmployeeEntity emp : employees) {
                if (results.size() >= 50)
                    break;
                results.add(mapEmployeeToMap(emp));
            }

            return toJson(results);

        } catch (Exception e) {
            log.error("Error searching employees: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Lấy danh sách chấm công hôm nay
     */
    @Tool(name = "get_today_attendance", description = "Lấy danh sách chấm công hôm nay. " +
            "Bao gồm: employee_id, full_name, check_in, check_out, status, worked_hours.")
    public String getTodayAttendance() {
        log.info("Getting today's attendance");

        try {
            LocalDate today = LocalDate.now();
            List<AttendanceEntity> attendances = attendanceRepository.findByWorkDate(today);

            // Join với employee để lấy tên
            Set<UUID> employeeIds = new HashSet<>();
            for (AttendanceEntity att : attendances) {
                employeeIds.add(att.getEmployeeId());
            }

            Map<UUID, EmployeeEntity> employeeMap = new HashMap<>();
            if (!employeeIds.isEmpty()) {
                for (EmployeeEntity emp : employeeRepository.findAllById(employeeIds)) {
                    employeeMap.put(emp.getId(), emp);
                }
            }

            List<Map<String, Object>> results = new ArrayList<>();
            for (AttendanceEntity att : attendances) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("attendance_id", att.getId());
                map.put("employee_id", att.getEmployeeId());

                EmployeeEntity emp = employeeMap.get(att.getEmployeeId());
                if (emp != null) {
                    map.put("employee_name", emp.getFullName());
                    map.put("employee_code", emp.getEmployeeCode());
                }

                map.put("work_date", att.getWorkDate());
                map.put("check_in", att.getCheckIn());
                map.put("check_out", att.getCheckOut());
                map.put("status", att.getStatus());
                map.put("worked_hours", att.getWorkedHours());
                results.add(map);
            }

            return toJson(results);

        } catch (Exception e) {
            log.error("Error getting today's attendance: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Lấy danh sách nhân viên với giới hạn
     */
    @Tool(name = "get_employee_list", description = "Lấy danh sách nhân viên với giới hạn số lượng.")
    public String getEmployeeList(
            @ToolParam(description = "Số lượng nhân viên tối đa (mặc định 20)") int limit) {

        log.info("Getting employee list with limit: {}", limit);

        try {
            int actualLimit = Math.min(limit, 100);

            List<EmployeeEntity> allEmployees = employeeRepository.findAll();
            List<Map<String, Object>> results = new ArrayList<>();

            int count = 0;
            for (EmployeeEntity emp : allEmployees) {
                if (count >= actualLimit)
                    break;
                results.add(mapEmployeeToMap(emp));
                count++;
            }

            return toJson(results);

        } catch (Exception e) {
            log.error("Error getting employee list: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Lấy thống kê nhân viên theo trạng thái
     */
    @Tool(name = "get_employee_statistics", description = "Lấy thống kê tổng quan về nhân viên. " +
            "Bao gồm: tổng số, theo trạng thái (ACTIVE, PROBATION, SUSPENDED, etc.)")
    public String getEmployeeStatistics() {
        log.info("Getting employee statistics");

        try {
            Map<String, Object> stats = new LinkedHashMap<>();

            long total = employeeRepository.count();
            stats.put("total_employees", total);

            long active = employeeRepository.countByStatus(EmploymentStatus.ACTIVE);
            long probation = employeeRepository.countByStatus(EmploymentStatus.PROBATION);
            long suspended = employeeRepository.countByStatus(EmploymentStatus.SUSPENDED);
            long resigned = employeeRepository.countByStatus(EmploymentStatus.RESIGNED);
            long terminated = employeeRepository.countByStatus(EmploymentStatus.TERMINATED);

            stats.put("active", active);
            stats.put("probation", probation);
            stats.put("suspended", suspended);
            stats.put("resigned", resigned);
            stats.put("terminated", terminated);

            List<Map<String, Object>> resultList = new ArrayList<>();
            resultList.add(stats);
            return toJson(resultList);

        } catch (Exception e) {
            log.error("Error getting employee statistics: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Lấy thông tin chấm công theo ngày cụ thể
     */
    @Tool(name = "get_attendance_by_date", description = "Lấy danh sách chấm công theo ngày cụ thể. " +
            "Ngày format: YYYY-MM-DD (ví dụ: 2024-01-15)")
    public String getAttendanceByDate(
            @ToolParam(description = "Ngày cần lấy thông tin (format: YYYY-MM-DD)") String date) {

        log.info("Getting attendance for date: {}", date);

        try {
            LocalDate workDate = LocalDate.parse(date);
            List<AttendanceEntity> attendances = attendanceRepository.findByWorkDate(workDate);

            // Join với employee để lấy tên
            Set<UUID> employeeIds = new HashSet<>();
            for (AttendanceEntity att : attendances) {
                employeeIds.add(att.getEmployeeId());
            }

            Map<UUID, EmployeeEntity> employeeMap = new HashMap<>();
            if (!employeeIds.isEmpty()) {
                for (EmployeeEntity emp : employeeRepository.findAllById(employeeIds)) {
                    employeeMap.put(emp.getId(), emp);
                }
            }

            List<Map<String, Object>> results = new ArrayList<>();
            for (AttendanceEntity att : attendances) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("attendance_id", att.getId());
                map.put("employee_id", att.getEmployeeId());

                EmployeeEntity emp = employeeMap.get(att.getEmployeeId());
                if (emp != null) {
                    map.put("employee_name", emp.getFullName());
                    map.put("employee_code", emp.getEmployeeCode());
                }

                map.put("work_date", att.getWorkDate());
                map.put("check_in", att.getCheckIn());
                map.put("check_out", att.getCheckOut());
                map.put("status", att.getStatus());
                map.put("worked_hours", att.getWorkedHours());
                results.add(map);
            }

            return toJson(results);

        } catch (Exception e) {
            log.error("Error getting attendance by date: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    // Helper methods

    private Map<String, Object> mapEmployeeToMap(EmployeeEntity emp) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", emp.getId());
        map.put("employee_code", emp.getEmployeeCode());
        map.put("full_name", emp.getFullName());
        map.put("gender", emp.getGender());
        map.put("dob", emp.getDob());
        map.put("phone", emp.getPhone());
        map.put("work_email", emp.getWorkEmail());
        map.put("personal_email", emp.getPersonalEmail());
        map.put("status", emp.getStatus());
        map.put("hire_date", emp.getHireDate());
        map.put("probation_end_date", emp.getProbationEndDate());
        map.put("employment_type", emp.getEmploymentType());
        return map;
    }

    private String toJson(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            sb.append("{");
            
            int j = 0;
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(formatValue(entry.getValue()));
                if (++j < row.size()) {
                    sb.append(",");
                }
            }
            
            sb.append("}");
            if (i < data.size() - 1) {
                sb.append(",");
            }
        }
        
        sb.append("]");
        return sb.toString();
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

