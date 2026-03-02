package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.atlas_erp.hr_service.enums.EmploymentStatus;
import com.atlas_erp.hr_service.enums.GenderType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileResponse {

    private UUID id;
    private String employeeCode;
    private String fullName;
    private String avatarUrl;
    private GenderType gender;
    private EmploymentStatus status;

    private ManagerDTO manager;
    private PersonalInfo personal;
    private JobInfo job;

    private List<AssignmentHistoryResponse> history;
    private List<SkillResponse> skills;
    private List<ContractResponse> contracts;

    @Data
    @Builder
    @NoArgsConstructor // BẮT BUỘC THÊM
    @AllArgsConstructor // BẮT BUỘC THÊM
    public static class PersonalInfo {
        private LocalDate dob;
        private String nationalId;
        private String permanentAddress;
        private String currentAddress;
        private String hometown;
        private String personalEmail;
        private String workEmail;
        private String phone;
        private String maritalStatus;
        private Integer dependentsCount;
    }

    @Data
    @Builder
    @NoArgsConstructor // BẮT BUỘC THÊM
    @AllArgsConstructor // BẮT BUỘC THÊM
    public static class JobInfo {
        private UUID departmentId;
        private String departmentName;
        private UUID teamId;
        private String teamName;
        private UUID jobTitleId;
        private String jobTitleName;
        private String gradeLevel;
        private String employmentType;
        private LocalDate joinDate;
        private LocalDate probationEndDate;
    }
}
