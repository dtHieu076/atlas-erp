package com.atlas_erp.ai_service.Config;

import com.atlas_erp.ai_service.tools.HrTools;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class HrToolConfig {

    @Bean
    @Description("Thực thi câu truy vấn SQL để lấy dữ liệu HR. " +
            "CHỈ sử dụng câu lệnh SELECT (read-only). " +
            "Không được phép sử dụng INSERT, UPDATE, DELETE, DROP, TRUNCATE. " +
            "Ví dụ: SELECT COUNT(*) FROM employee WHERE status = 'ACTIVE'")
    Function<QueryHrDatabaseRequest, String> queryHrDatabase(HrTools hrTools) {
        return req -> hrTools.queryHrDatabase(req.sql());
    }

    @Bean
    @Description("Tìm kiếm nhân viên theo tên. " +
            "Trả về thông tin cơ bản: id, employee_code, full_name, work_email, phone, status, hire_date.")
    Function<SearchEmployeesRequest, String> searchEmployees(HrTools hrTools) {
        return req -> hrTools.searchEmployees(req.name());
    }

    @Bean
    @Description("Lấy danh sách chấm công hôm nay. " +
            "Bao gồm: employee_id, full_name, check_in, check_out, status, worked_hours.")
    Supplier<String> getTodayAttendance(HrTools hrTools) {
        return hrTools::getTodayAttendance;
    }

    @Bean
    @Description("Lấy danh sách nhân viên với giới hạn số lượng.")
    Function<GetEmployeeListRequest, String> getEmployeeList(HrTools hrTools) {
        return req -> hrTools.getEmployeeList(req.limit());
    }

    @Bean
    @Description("Lấy thống kê tổng quan về nhân viên. " +
            "Bao gồm: tổng số, theo trạng thái (ACTIVE, PROBATION, SUSPENDED, etc.)")
    Supplier<String> getEmployeeStatistics(HrTools hrTools) {
        return hrTools::getEmployeeStatistics;
    }

    @Bean
    @Description("Lấy danh sách chấm công theo ngày cụ thể. " +
            "Ngày format: YYYY-MM-DD (ví dụ: 2024-01-15)")
    Function<GetAttendanceByDateRequest, String> getAttendanceByDate(HrTools hrTools) {
        return req -> hrTools.getAttendanceByDate(req.date());
    }

    // DTO Records

    public record QueryHrDatabaseRequest(String sql) {
    }

    public record SearchEmployeesRequest(String name) {
    }

    public record GetEmployeeListRequest(int limit) {
    }

    public record GetAttendanceByDateRequest(String date) {
    }
}
