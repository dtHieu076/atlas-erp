package org.atlas_erp.mail.controller;

import org.atlas_erp.mail.dto.MailEventDTO;
import org.atlas_erp.mail.dto.StatsDTO;
import org.atlas_erp.mail.dto.TemplateDTO;
import org.atlas_erp.mail.dto.CampaignPageDTO;
import org.atlas_erp.mail.dto.ApiResponse;
import org.atlas_erp.mail.service.MailJobQueue;
import org.atlas_erp.mail.service.TemplateService;
import org.atlas_erp.mail.service.CampaignService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST controller for testing/fallback mail sending.
 * POST /api/mail/send - enqueue MailEventDTO directly.
 */
@ApplicationScoped
@Path("/api/mail")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MailRestController {

    @Inject
    MailJobQueue jobQueue;

    @Inject
    TemplateService templateService;

    @Inject
    CampaignService campaignService;

    @POST
    @Path("/send")
    public Response sendMail(@Valid MailEventDTO event) {
        jobQueue.enqueue(event);
        return Response.ok(ApiResponse.success("Enqueued " + event.getRecipients().size() + " mail jobs"))
                .build();
    }

    @GET
    @Path("/templates")
    public Response getTemplates() {
        List<TemplateDTO> templates = templateService.listTemplates();
        return Response.ok(ApiResponse.success("Fetched templates successfully", templates))
                .build();
    }

    @GET
    @Path("/campaigns")
    public Response getCampaigns(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        CampaignPageDTO campaigns = campaignService.getCampaigns(page, size);
        return Response.ok(ApiResponse.success("Fetched campaigns successfully", campaigns))
                .build();
    }

    @GET
    @Path("/stats")
    public Response getStats() {
        StatsDTO stats = campaignService.getStats();
        return Response.ok(ApiResponse.success("Stats fetched successfully", stats))
                .build();
    }
}
