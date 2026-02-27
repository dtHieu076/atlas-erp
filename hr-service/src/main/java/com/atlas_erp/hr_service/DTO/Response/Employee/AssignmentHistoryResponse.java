package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentHistoryResponse {

    private UUID departmentId;
    private String departmentName;

    private UUID jobTitleId;
    private String jobTitleName;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String changeReason;
}