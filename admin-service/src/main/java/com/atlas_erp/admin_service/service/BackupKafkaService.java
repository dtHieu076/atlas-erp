package com.atlas_erp.admin_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupKafkaService {

    private static final String COMMANDS_TOPIC = "erp-backup-commands";
    private static final String STATUS_TOPIC = "erp-backup-status";

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Send backup command to trigger services
     */
    public void sendBackupCommand() {
        kafkaTemplate.send(COMMANDS_TOPIC, "admin-service-key", "Cần backup");
        log.info("Sent backup command to topic: {}", COMMANDS_TOPIC);
    }

    /**
     * Will be called by status listener to track responses
     */
    public void handleStatusResponse(String serviceName, String message) {
        log.info("Received backup status from {}: {}", serviceName, message);
    }
}
