package com.atlas_erp.production_service.Dto.Response.ProductionExecution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinishExecutionResponse {
    private Boolean success;

    public static FinishExecutionResponse success() {
        return new FinishExecutionResponse(true);
    }

    public static FinishExecutionResponse fail() {
        return new FinishExecutionResponse(false);
    }
}
