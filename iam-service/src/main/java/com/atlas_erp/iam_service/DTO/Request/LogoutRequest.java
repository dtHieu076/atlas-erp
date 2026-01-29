package com.atlas_erp.iam_service.DTO.Request;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
