package com.atlas_erp.payroll_service.Dto.Response.Payroll;

import lombok.Data;
import java.util.List;

import com.atlas_erp.payroll_service.Dto.PayrollDetailItemDTO;

@Data
public class PayrollListResponse {

    private List<PayrollDetailItemDTO> list;
    private Long total;
}
