package com.atlas_erp.hr_service.DTO.Response;

import com.atlas_erp.hr_service.DTO.EmployeeContractDTO;
import lombok.Data;
import java.util.List;

// API: GET /employees/{id}/contracts
@Data
public class EmployeeContractListResponse {

    private List<EmployeeContractDTO> list;
}
