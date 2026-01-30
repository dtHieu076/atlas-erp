package com.atlas_erp.iam_service.Dto.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSuccessResponse {

    private boolean success;

    public static BaseSuccessResponse success() {
        return new BaseSuccessResponse(true);
    }

    public static BaseSuccessResponse fail() {
        return new BaseSuccessResponse(false);
    }
}
