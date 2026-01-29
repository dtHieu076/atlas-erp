package com.atlas_erp.hr_service.DTO.Response;

import com.atlas_erp.hr_service.DTO.EmployeeItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class EmployeeListResponse {

    private List<EmployeeItemDTO> list;
    private Long total;
}
