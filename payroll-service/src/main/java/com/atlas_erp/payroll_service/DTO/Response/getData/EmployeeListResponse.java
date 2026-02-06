package com.atlas_erp.payroll_service.Dto.Response.getData;

import lombok.Data;
import java.util.List;

/**
 * DTO đại diện cho phản hồi danh sách nhân viên từ HR Service
 */
@Data
public class EmployeeListResponse {

    private List<EmployeeDTO> list;
    private Long total;

    /**
     * Thông tin chi tiết của từng nhân viên
     */
    @Data
    public static class EmployeeDTO {
        private String id;
        private String employeeCode;
        private String fullName;
        private String departmentId;
        private String status; // Thêm để hỗ trợ lọc nhân viên ACTIVE
    }
}