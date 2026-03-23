package com.atlas_erp.mail_service.controller;

import com.atlas_erp.mail_service.dto.MailEventDTO;
import com.atlas_erp.mail_service.queue.MailJobQueue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@Valid @RequestBody MailEventDTO event) {
        jobQueue.enqueue(event);
        log.info("REST API enqueued {} recipients", event.getRecipients().size());
        return ResponseEntity.ok("Enqueued " + event.getRecipients().size() + " mail jobs successfully");
    }
}
