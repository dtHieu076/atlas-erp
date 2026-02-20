package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Request.Contract.*;
import com.atlas_erp.hr_service.Dto.Response.Contract.*;

import java.util.List;
import java.util.UUID;

public interface ContractService {

    EmployeeContractListResponse getContractsByEmployee(UUID employeeId);

    CreateContractResponse createContract(CreateContractRequest request);

    List<ContractResponse> getAllContracts();

    ContractDetailResponse getContractDetail(UUID id);
}
