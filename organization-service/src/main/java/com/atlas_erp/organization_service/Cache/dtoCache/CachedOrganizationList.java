package com.atlas_erp.organization_service.Cache.dtoCache;

import com.atlas_erp.organization_service.Dto.Response.Organization.OrganizationListResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedOrganizationList implements Serializable {
    private static final long serialVersionUID = 1L;
    private OrganizationListResponse data;
}
