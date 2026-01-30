package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.Data;
import java.util.List;

import com.atlas_erp.hr_service.Dto.EmployeeItemDTO;

@Data
public class EmployeeListResponse {

    private List<EmployeeItemDTO> list;
    private Long total;
}
