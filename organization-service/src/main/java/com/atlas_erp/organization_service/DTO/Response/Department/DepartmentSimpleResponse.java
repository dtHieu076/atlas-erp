package com.atlas_erp.organization_service.Dto.Response.Department;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentSimpleResponse {
    private UUID Id;
    private String Name;
}
