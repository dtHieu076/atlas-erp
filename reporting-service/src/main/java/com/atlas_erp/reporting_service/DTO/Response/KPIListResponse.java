package com.atlas_erp.reporting_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class KPIListResponse {

    private List<KPIDto> list;

    @Data
    public static class KPIDto {
        private String code;
        private String name;
        private BigDecimal target;
        private BigDecimal actual;
        private BigDecimal achievementRate;
        private String status;
    }
}
