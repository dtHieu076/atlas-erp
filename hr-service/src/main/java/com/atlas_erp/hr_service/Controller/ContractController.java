package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.Contract.CreateContractRequest;
import com.atlas_erp.hr_service.Dto.Response.Contract.CreateContractResponse;
import com.atlas_erp.hr_service.Dto.Response.Contract.EmployeeContractListResponse;
import com.atlas_erp.hr_service.Service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    // 4️⃣ GET /employees/{id}/contracts
    @GetMapping("/employees/{id}/contracts")
    public EmployeeContractListResponse getContractsByEmployee(
            @PathVariable UUID id) {
        return contractService.getContractsByEmployee(id);
    }

    // 5️⃣ POST /contracts
    @PostMapping("/contracts")
    public CreateContractResponse createContract(
            @RequestBody CreateContractRequest request) {
        return contractService.createContract(request);
    }
}
