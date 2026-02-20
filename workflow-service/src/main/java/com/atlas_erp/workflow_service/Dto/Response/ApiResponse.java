package com.atlas_erp.workflow_service.Dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
}
