// File: src/main/java/org/atlas_erp/resource/WorkflowResource.java
package org.atlas_erp.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import org.atlas_erp.dto.ApiResponse;
import org.atlas_erp.dto.WorkflowAllListResponse;
import org.atlas_erp.service.WorkflowService;

import java.util.List;

@Path("/workflows") // Tương đương @RequestMapping
@RequiredArgsConstructor
public class WorkflowResource {

    private final WorkflowService workflowService;

    @GET
    @Path("/all") // Tương đương @GetMapping("/all")
    @Produces(MediaType.APPLICATION_JSON) // Khai báo trả về JSON
    public RestResponse<ApiResponse<List<WorkflowAllListResponse>>> getAllWorkflows() {

        List<WorkflowAllListResponse> response = workflowService.getAllWorkflows();

        ApiResponse<List<WorkflowAllListResponse>> apiResponse = ApiResponse.<List<WorkflowAllListResponse>>builder()
                .success(true)
                .message("Successfully retrieved all workflows")
                .data(response)
                .build();

        // Tương đương ResponseEntity.ok()
        return RestResponse.ok(apiResponse);
    }
}