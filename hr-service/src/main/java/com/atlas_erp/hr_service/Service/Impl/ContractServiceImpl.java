package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Request.Contract.*;
import com.atlas_erp.hr_service.Dto.Response.Contract.*;
// import com.atlas_erp.hr_service.Repository.EmployeeContractRepository;
import com.atlas_erp.hr_service.Service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    // private final EmployeeContractRepository contractRepository;

    @Override
    public EmployeeContractListResponse getContractsByEmployee(UUID employeeId) {

        EmployeeContractListResponse response = new EmployeeContractListResponse();
        response.setList(List.of()); // trả về danh sách rỗng (stub)

        return response;
    }

    @Override
    public CreateContractResponse createContract(CreateContractRequest request) {

        CreateContractResponse response = new CreateContractResponse();
        response.setId(UUID.randomUUID().toString()); // trả về id giả lập (stub)

        return response;
    }
}
