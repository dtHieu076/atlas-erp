package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.EmployeeContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeContractRepository
                extends JpaRepository<EmployeeContractEntity, UUID> {

        List<EmployeeContractEntity> findByEmployeeId(UUID employeeId);

        List<EmployeeContractEntity> findByEmployeeIdAndStatus(
                        UUID employeeId,
                        String status);// Lấy danh sách hợp đồng của nhân viên, ưu tiên hợp đồng mới nhất lên đầu

        List<EmployeeContractEntity> findByEmployeeIdOrderByStartDateDesc(UUID employeeId);
}
