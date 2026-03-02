package com.atlas_erp.hr_service.Dto.Response.JobTitle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTitleResponse {

    private String code; // Mã chức danh
    private String name; // Tên chức danh
    private String level; // Cấp bậc (map từ gradeLevel)
    private String dept; // id phòng ban (KHÔNG trả UUID)
    private String departmentName;
    private String desc; // Mô tả (map từ description)
    private String status; // ACTIVE / INACTIVE
}