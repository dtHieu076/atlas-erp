package com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod;

import lombok.Data;
import java.util.List;

import com.atlas_erp.payroll_service.Dto.PayrollPeriodItemDTO;

@Data
public class PayrollPeriodListResponse {

    private List<PayrollPeriodItemDTO> list;
    private Long total;
}
