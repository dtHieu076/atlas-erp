package com.atlas_erp.payroll_service.Dto.Response.Payroll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.atlas_erp.payroll_service.Dto.PayrollDetailItemDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollListResponse {

    private List<PayrollDetailItemDTO> list;
    private Long total;
}
