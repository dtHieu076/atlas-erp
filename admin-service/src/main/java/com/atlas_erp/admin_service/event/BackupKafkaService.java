package com.atlas_erp.admin_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupKafkaService {

    private static final String COMMANDS_TOPIC = "erp-backup-commands";

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Send backup command with Job ID to hr-service
     */
    public void sendBackupCommand(UUID jobId) {
        String message = jobId.toString() + ":Cần backup";
        kafkaTemplate.send(COMMANDS_TOPIC, "admin-backup-job-" + jobId, message);
        log.info("Sent backup command for job {} to topic {}", jobId, COMMANDS_TOPIC);
    }
}
