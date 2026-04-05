package org.atlas_erp.dto;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class ActivityLogQueryRequest {
    
    @QueryParam("userId")
    private String userId;
    
    @QueryParam("sessionId")
    private String sessionId;
    
    @QueryParam("action")
    private String action;
    
    @QueryParam("startDate")
    private Long startDate; // clientTimestamp ms
    
    @QueryParam("endDate")
    private Long endDate; // clientTimestamp ms

    @QueryParam("page")
    @DefaultValue("0")
    private int page = 0;

    @QueryParam("size")
    @DefaultValue("20")
    private int size = 20;

    @QueryParam("sort")
    @DefaultValue("clientTimestamp,desc")
    private String sort = "clientTimestamp,desc"; 
}