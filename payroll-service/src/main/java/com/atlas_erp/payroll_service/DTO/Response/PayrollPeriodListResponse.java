package com.atlas_erp.payroll_service.DTO.Response;

import com.atlas_erp.payroll_service.DTO.PayrollPeriodItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class PayrollPeriodListResponse {

    private List<PayrollPeriodItemDTO> list;
    private Long total;
}
