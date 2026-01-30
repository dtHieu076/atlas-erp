package com.atlas_erp.hr_service.Dto.Response.Contract;

import lombok.Data;
import java.util.List;

import com.atlas_erp.hr_service.Dto.EmployeeContractDTO;

// API: GET /employees/{id}/contracts
@Data
public class EmployeeContractListResponse {

    private List<EmployeeContractDTO> list;
}
