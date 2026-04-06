package org.atlas_erp.mail.dto;

import lombok.Data;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Data
@RegisterForReflection
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Operation successful", data);
    }
}
