package com.atlas_erp.ai_service.Config;

import com.atlas_erp.ai_service.tools.HrTools;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
@RegisterReflectionForBinding({
        HrToolConfig.QueryHrDatabaseRequest.class,
        HrToolConfig.SearchEmployeesRequest.class,
        HrToolConfig.GetEmployeeListRequest.class,
        HrToolConfig.GetAttendanceByDateRequest.class,
        HrToolConfig.IdRequest.class,
        HrToolConfig.DaysRequest.class,
        HrToolConfig.GenderRatioRequest.class
})
public class HrToolConfig {

    @Bean
    @Description("Thực thi câu truy vấn SQL SELECT để lấy dữ liệu HR.")
    Function<QueryHrDatabaseRequest, String> queryHrDatabase(HrTools hrTools) {
        return req -> hrTools.queryHrDatabase(new HrTools.SqlQueryRequest(req.sql()));
    }

    @Bean
    @Description("Tìm kiếm nhân viên theo tên.")
    Function<SearchEmployeesRequest, String> searchEmployees(HrTools hrTools) {
        return req -> hrTools.searchEmployees(new HrTools.SearchRequest(req.name()));
    }

    @Bean
    @Description("Lấy danh sách chấm công hôm nay.")
    Supplier<String> getTodayAttendance(HrTools hrTools) {
        return hrTools::getTodayAttendance;
    }

    @Bean
    @Description("Lấy danh sách nhân viên với giới hạn số lượng.")
    Function<GetEmployeeListRequest, String> getEmployeeList(HrTools hrTools) {
        return req -> hrTools.getEmployeeList(new HrTools.LimitRequest(req.limit()));
    }

    @Bean
    @Description("Lấy thống kê tổng quan về nhân viên theo trạng thái.")
    Supplier<String> getEmployeeStatistics(HrTools hrTools) {
        return hrTools::getEmployeeStatistics;
    }

    @Bean
    @Description("Lấy danh sách chấm công theo ngày cụ thể (YYYY-MM-DD).")
    Function<GetAttendanceByDateRequest, String> getAttendanceByDate(HrTools hrTools) {
        return req -> hrTools.getAttendanceByDate(new HrTools.DateRequest(req.date()));
    }

    @Bean
    @Description("Thống kê số lượng và tỷ lệ phần trăm nam/nữ trong công ty.")
    Function<GenderRatioRequest, String> getGenderRatio(HrTools hrTools) {
        return req -> hrTools.getGenderRatio(new HrTools.GenderRatioRequest(req.departmentName()));
    }

    @Bean
    @Description("Lấy toàn bộ thông tin chi tiết của một nhân viên dựa trên ID.")
    Function<IdRequest, String> getEmployeeDetail(HrTools hrTools) {
        return req -> hrTools.getEmployeeDetail(new HrTools.IdRequest(req.id()));
    }

    @Bean
    @Description("Lấy danh sách nhân viên mới gia nhập trong số ngày gần đây nhất.")
    Function<DaysRequest, String> getNewHires(HrTools hrTools) {
        return req -> hrTools.getNewHires(new HrTools.DaysRequest(req.days()));
    }

    @Bean
    @Description("Lấy danh sách nhân viên đang nghỉ phép hôm nay.")
    Supplier<String> getEmployeesOnLeave(HrTools hrTools) {
        return hrTools::getEmployeesOnLeave;
    }

    @Bean
    @Description("Lấy danh sách nhân viên có sinh nhật trong tháng này.")
    Supplier<String> getUpcomingBirthdays(HrTools hrTools) {
        return hrTools::getUpcomingBirthdays;
    }

    // --- DTO Records phục vụ Function Calling ---
    public record QueryHrDatabaseRequest(String sql) {
    }

    public record SearchEmployeesRequest(String name) {
    }

    public record GetEmployeeListRequest(int limit) {
    }

    public record GetAttendanceByDateRequest(String date) {
    }

    public record IdRequest(UUID id) {
    }

    public record DaysRequest(int days) {
    }

    public record GenderRatioRequest(String departmentName) {
    }
}