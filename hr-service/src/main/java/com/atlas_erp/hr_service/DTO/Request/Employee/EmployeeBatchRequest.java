package com.atlas_erp.hr_service.Dto.Request.Employee;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class EmployeeBatchRequest {
    private List<UUID> ids;
}
