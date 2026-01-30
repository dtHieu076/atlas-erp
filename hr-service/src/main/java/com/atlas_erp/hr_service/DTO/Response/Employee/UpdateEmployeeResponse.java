package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeResponse {

    private Boolean success;

    public static UpdateEmployeeResponse success() {
        return new UpdateEmployeeResponse(true);
    }

    public static UpdateEmployeeResponse fail() {
        return new UpdateEmployeeResponse(false);
    }
}
