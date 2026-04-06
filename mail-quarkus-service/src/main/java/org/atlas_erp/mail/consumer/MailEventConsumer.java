package org.atlas_erp.mail.consumer;

import org.atlas_erp.mail.dto.MailEventDTO;
import org.atlas_erp.mail.service.MailJobQueue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 * Kafka consumer listening to mail_requests topic using Reactive Messaging.
 * Validates and enqueues MailEventDTO to queue.
 */
@ApplicationScoped
@Slf4j
public class MailEventConsumer {

    @Inject
    MailJobQueue jobQueue;

    @Incoming("mail-requests")
    public void listen(MailEventDTO event) {
        try {
            log.info("Received mail event with {} recipients", event.getRecipients().size());
            jobQueue.enqueue(event);
            log.info("Enqueued {} recipients from event", event.getRecipients().size());
        } catch (Exception e) {
            log.error("Error processing mail event: {}", e.getMessage(), e);
        }
    }
}
