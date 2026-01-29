package com.atlas_erp.payroll_service.DTO.Response;

import com.atlas_erp.payroll_service.DTO.PayrollDetailItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class PayrollListResponse {

    private List<PayrollDetailItemDTO> list;
    private Long total;
}
