package com.atlas_erp.mail_service.consumer;

import com.atlas_erp.mail_service.dto.MailEventDTO;
import com.atlas_erp.mail_service.queue.MailJobQueue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer listening to mail_requests topic.
 * Validates and enqueues MailEventDTO to queue.
 */
@Component
@Validated
@RequiredArgsConstructor
@Slf4j
public class MailEventConsumer {

    private final MailJobQueue jobQueue;

    @KafkaListener(topics = "${app.mail.topic:mail_requests}", groupId = "mail-service-group")
    public void listen(@Valid MailEventDTO event) {
        log.info("Received mail event with {} recipients", event.getRecipients().size());
        jobQueue.enqueue(event);
        log.info("Enqueued {} recipients from event", event.getRecipients().size());
    }
}
