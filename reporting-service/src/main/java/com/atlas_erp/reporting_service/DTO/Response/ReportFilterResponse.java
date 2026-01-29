package com.atlas_erp.reporting_service.DTO.Response;

import lombok.Data;
import java.util.List;

@Data
public class ReportFilterResponse {

    private String reportType;
    private List<ReportFilterDto> filters;

    @Data
    public static class ReportFilterDto {
        private String filterKey;
        private String filterLabel;
        private String filterType; // DATE | SELECT | MULTI_SELECT | NUMBER
        private Boolean required;
    }
}
