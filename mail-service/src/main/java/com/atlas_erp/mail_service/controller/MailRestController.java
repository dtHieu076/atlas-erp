package com.atlas_erp.mail_service.controller;

import com.atlas_erp.mail_service.dto.MailEventDTO;
import com.atlas_erp.mail_service.dto.StatsDTO;
import com.atlas_erp.mail_service.dto.TemplateDTO;
import com.atlas_erp.mail_service.queue.MailJobQueue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.atlas_erp.mail_service.service.TemplateService;
import com.atlas_erp.mail_service.service.CampaignService;
import com.atlas_erp.mail_service.dto.ApiResponse;
import com.atlas_erp.mail_service.dto.CampaignPageDTO;

/**
 * REST controller for testing/fallback mail sending.
 * POST /api/mail/send - enqueue MailEventDTO directly.
 */
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
@Slf4j
public class MailRestController {

    private final MailJobQueue jobQueue;
    private final TemplateService templateService;
    private final CampaignService campaignService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendMail(@Valid @RequestBody MailEventDTO event) {
        jobQueue.enqueue(event);
        log.info("REST API enqueued {} recipients", event.getRecipients().size());
        return ResponseEntity.ok(ApiResponse.success("Enqueued " + event.getRecipients().size() + " mail jobs"));
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<TemplateDTO>>> getTemplates() {
        List<TemplateDTO> templates = templateService.listTemplates();
        return ResponseEntity.ok(ApiResponse.success("Fetched templates successfully", templates));
    }

    @GetMapping("/campaigns")
    public ResponseEntity<ApiResponse<CampaignPageDTO>> getCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        CampaignPageDTO campaigns = campaignService.getCampaigns(page, size);
        return ResponseEntity.ok(ApiResponse.success("Fetched campaigns successfully", campaigns));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<StatsDTO>> getStats() {
        StatsDTO stats = campaignService.getStats();
        return ResponseEntity.ok(ApiResponse.success("Stats fetched successfully", stats));
    }
}
