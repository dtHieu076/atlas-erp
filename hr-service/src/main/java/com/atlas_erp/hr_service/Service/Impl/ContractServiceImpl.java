package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Cache.serviceCache.ContractCacheService;
import com.atlas_erp.hr_service.Dto.EmployeeContractDTO;
import com.atlas_erp.hr_service.Dto.Request.Contract.*;
import com.atlas_erp.hr_service.Dto.Response.Contract.*;
import com.atlas_erp.hr_service.Entity.EmployeeContractEntity;
import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeContractCreatedEvent;
import com.atlas_erp.hr_service.Event.serviceEvent.ContractEventService;
import com.atlas_erp.hr_service.Repository.EmployeeContractRepository;
import com.atlas_erp.hr_service.Repository.EmployeeRepository;
import com.atlas_erp.hr_service.Service.ContractService;
import lombok.RequiredArgsConstructor;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final EmployeeContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private ContractCacheService contractCacheService;
    private final ContractEventService contractEventService;

    @Override
    public EmployeeContractListResponse getContractsByEmployee(UUID employeeId) {
        // 1. Kiểm tra Cache trước
        EmployeeContractListResponse cached = contractCacheService.getContractList(employeeId);
        if (cached != null) {
            return cached;
        }

        // 2. Kiểm tra nhân viên có tồn tại không (Đảm bảo tính toàn vẹn dữ liệu)
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Không tìm thấy nhân viên: " + employeeId);
        }

        // 3. Truy vấn Database
        List<EmployeeContractEntity> entities = contractRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);

        // 4. Map Entity sang DTO
        List<EmployeeContractDTO> dtoList = entities.stream().map(entity -> {
            EmployeeContractDTO dto = new EmployeeContractDTO();
            dto.setId(entity.getId().toString());
            dto.setEmployeeId(entity.getEmployeeId().toString());
            dto.setContractType(entity.getContractType());
            dto.setStartDate(entity.getStartDate());
            dto.setEndDate(entity.getEndDate());
            dto.setSalary(entity.getBaseSalary()); // Lấy baseSalary từ Entity
            dto.setStatus(entity.getStatus());
            return dto;
        }).toList();

        EmployeeContractListResponse response = new EmployeeContractListResponse();
        response.setList(dtoList);

        // 5. Lưu vào Cache cho lần sau
        contractCacheService.putContractList(employeeId, response);

        return response;
    }

    @Override
    @Transactional
    public CreateContractResponse createContract(CreateContractRequest request) {
        UUID empId = UUID.fromString(request.getEmployeeId());

        // 1. Kiểm tra nhân viên tồn tại
        if (!employeeRepository.existsById(empId)) {
            throw new ResourceNotFoundException("Không tìm thấy nhân viên: " + empId);
        }

        // 2. Validate logic ngày tháng
        if (request.getEndDate() != null && request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc.");
        }

        // 3. Kiểm tra Overlap (Chống trùng lặp hợp đồng đang hiệu lực)
        List<EmployeeContractEntity> existingContracts = contractRepository.findByEmployeeIdOrderByStartDateDesc(empId);
        boolean hasOverlap = existingContracts.stream()
                .anyMatch(c -> "ACTIVE".equals(c.getStatus()) &&
                        (request.getStartDate().isBefore(c.getEndDate()) || c.getEndDate() == null));

        if (hasOverlap) {
            throw new IllegalStateException("Nhân viên đang có một hợp đồng ACTIVE khác trong thời gian này.");
        }

        // 4. Gọi Payroll Service validate period (Giả lập Call Service)
        // log.info("Calling Payroll Service to validate period for employee: {}",
        // empId);

        // 5. Lưu Contract Entity
        EmployeeContractEntity contract = new EmployeeContractEntity();
        contract.setEmployeeId(empId);
        contract.setContractType(request.getContractType());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setBaseSalary(request.getSalary());
        contract.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        // contract.setWorkingHours(request.getWorkingHours()); // Nếu entity có field
        // này

        contract = contractRepository.save(contract);

        // 6. Xử lý Cache: Xóa cache danh sách hợp đồng của nhân viên này
        contractCacheService.evictContractList(empId);

        // 7. Phát Event
        EmployeeContractCreatedEvent event = EmployeeContractCreatedEvent.builder()
                .contractId(contract.getId())
                .employeeId(empId)
                .salary(request.getSalary())
                .startDate(request.getStartDate())
                .contractType(request.getContractType())
                .build();
        contractEventService.publishContractCreatedEvent(event);

        return new CreateContractResponse(contract.getId().toString());
    }
}
