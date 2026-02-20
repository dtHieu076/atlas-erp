package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.Contract.CreateContractRequest;
import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Contract.ContractDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Contract.ContractResponse;
import com.atlas_erp.hr_service.Dto.Response.Contract.CreateContractResponse;
import com.atlas_erp.hr_service.Dto.Response.Contract.EmployeeContractListResponse;
import com.atlas_erp.hr_service.Service.ContractService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contracts")
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

    // 🔹 GET /contracts
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getAllContracts() {

        List<ContractResponse> data = contractService.getAllContracts();

        return ResponseEntity.ok(
                ApiResponse.success(data));
    }

    // 🔹 GET /contracts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractDetailResponse>> getContractDetail(
            @PathVariable UUID id) {

        ContractDetailResponse data = contractService.getContractDetail(id);

        return ResponseEntity.ok(
                ApiResponse.success(data));
    }
}
