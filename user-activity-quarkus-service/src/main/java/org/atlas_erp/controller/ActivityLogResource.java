package org.atlas_erp.controller;

import org.atlas_erp.dto.ActivityLogQueryRequest;
import org.atlas_erp.dto.ActivityLogRequest;
import org.atlas_erp.dto.ApiResponse;
import org.atlas_erp.service.LogQueryService;
import org.atlas_erp.service.TrackingService;

import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1") // Thay cho @RequestMapping
@Produces(MediaType.APPLICATION_JSON) // Mặc định trả về JSON
@Consumes(MediaType.APPLICATION_JSON) // Mặc định nhận JSON
public class ActivityLogResource {

    private final TrackingService trackingService;
    private final LogQueryService logQueryService;

    // Quarkus CDI hỗ trợ Constructor Injection giống hệt Spring Boot
    public ActivityLogResource(TrackingService trackingService, LogQueryService logQueryService) {
        this.trackingService = trackingService;
        this.logQueryService = logQueryService;
    }

    @POST // Thay cho @PostMapping
    @Path("/logs")
    public Response logActivity(
            ActivityLogRequest request, // Tự động map từ Body (không cần @RequestBody)
            @Context HttpServerRequest httpRequest) { // Lấy thông tin request bằng Vert.x
        try {
            trackingService.trackActivity(request, httpRequest);
            return Response.ok("Log sent to Kafka successfully").build(); // Thay cho ResponseEntity
        } catch (Exception e) {
            return Response.serverError().entity("Failed to process log: " + e.getMessage()).build();
        }
    }

    @GET // Thay cho @GetMapping
    @Path("/logs")
    public Response getActivityLogs(
            @BeanParam ActivityLogQueryRequest queryRequest, // Map query params vào DTO bằng @BeanParam
            @DefaultValue("0") @QueryParam("page") int page, // Spring Pageable không tồn tại ở Quarkus, ta dùng tham số rời
            @DefaultValue("20") @QueryParam("size") int size,
            @DefaultValue("clientTimestamp") @QueryParam("sort") String sort,
            @DefaultValue("DESC") @QueryParam("direction") String direction) {

        // LogQueryService sẽ được cấu hình lại để nhận các tham số phân trang này thay vì đối tượng Pageable
        Object logs = logQueryService.getActivityLogs(queryRequest, page, size, sort, direction);
        
        return Response.ok(ApiResponse.success(logs)).build();
    }
}
