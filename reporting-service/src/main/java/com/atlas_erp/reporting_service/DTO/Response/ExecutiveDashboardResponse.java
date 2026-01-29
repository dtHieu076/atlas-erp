package com.atlas_erp.reporting_service.DTO.Response;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class ExecutiveDashboardResponse {

    private BigDecimal revenue;
    private BigDecimal expense;
    private BigDecimal profit;
    private Integer totalEmployee;
    private BigDecimal productivityIndex;
    private Date snapshotDate;
}
