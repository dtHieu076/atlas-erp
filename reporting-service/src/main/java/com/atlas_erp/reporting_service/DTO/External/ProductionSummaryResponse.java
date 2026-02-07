package com.atlas_erp.reporting_service.Dto.External;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductionSummaryResponse {
    private String period;
    private BigDecimal totalActualProduction; // Sản lượng thực tế (m3 gỗ hoặc số thành phẩm)
    private String unit;
}