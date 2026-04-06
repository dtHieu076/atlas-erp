package org.atlas_erp.Resource;

import org.atlas_erp.DTO.ApiResponse;
import org.atlas_erp.DTO.OrganizationTreeResponse;
import org.atlas_erp.Service.OrganizationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {

    @Inject // Dùng @Inject thay cho RequiredArgsConstructor ở tầng API cho chuẩn Quarkus
    OrganizationService organizationService;

    @GET
    @Path("/tree")
    public Response getTree() {
        OrganizationTreeResponse tree = organizationService.getOrganizationTree();
        
        return Response.ok(
                ApiResponse.success("Fetch organization tree successfully", tree)
        ).build();
    }
}
