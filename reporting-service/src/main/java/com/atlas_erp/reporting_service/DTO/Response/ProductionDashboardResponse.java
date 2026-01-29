package com.atlas_erp.reporting_service.DTO.Response;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class ProductionDashboardResponse {

    private Integer totalPlanQty;
    private Integer totalActualQty;
    private BigDecimal defectRate;
    private BigDecimal onTimeRate;
    private BigDecimal utilizationRate;
    private Date snapshotDate;
}
