package com.atlas_erp.admin_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.atlas_erp.admin_service.dto.BackupResponseDto;
import com.atlas_erp.admin_service.dto.VeleroBackup;
import com.atlas_erp.admin_service.dto.VeleroBackupSpec;
import com.atlas_erp.admin_service.entity.BackupJob;
import com.atlas_erp.admin_service.enums.BackupStatus;
import com.atlas_erp.admin_service.repository.BackupJobRepository;
import com.atlas_erp.admin_service.service.VeleroBackupService;
import com.atlas_erp.admin_service.event.BackupKafkaService;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class VeleroBackupServiceImpl implements VeleroBackupService {

    private final BackupJobRepository backupJobRepository;
    private final BackupKafkaService backupKafkaService;

    private final KubernetesClient client = new KubernetesClientBuilder().build();

    @Value("${backup.required-services:hr-service}")
    private List<String> requiredServices;

    // Track completed services per job
    private final Map<UUID, Set<String>> completedServicesMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public BackupResponseDto triggerBackup(String targetNamespace) {
        String backupName = "manual-backup-" + Instant.now().toEpochMilli();

        // Create job with WAITING_HR status
        BackupJob job = new BackupJob(targetNamespace, BackupStatus.WAITING_HR);
        job.setVeleroBackupName(backupName);
        job = backupJobRepository.save(job);

        // Send Kafka command with job ID to hr-service
        backupKafkaService.sendBackupCommand(job.getId());

        log.info("Backup job {} created - WAITING_HR. Sent command to {}. Services expected: {}",
                job.getId(), requiredServices, requiredServices);

        return mapToDto(job);
    }

    /**
     * Called when all required services confirmed
     */
    @Transactional
    public void executeActualVeleroBackup(UUID jobId) {
        BackupJob job = backupJobRepository.findById(jobId).orElseThrow();

        try {
            log.info(">>>> [START] executeActualVeleroBackup for Job: {}", jobId);
            log.info("========== PREPARING TYPED DATA FOR VELERO ==========");

            // 1. Khởi tạo đối tượng VeleroBackup bằng Class tự định nghĩa
            VeleroBackup backupResource = new VeleroBackup();

            // Set Metadata
            backupResource.getMetadata().setName(job.getVeleroBackupName());
            backupResource.getMetadata().setNamespace("velero");

            // Set Spec
            VeleroBackupSpec spec = new VeleroBackupSpec();
            spec.setIncludedNamespaces(java.util.List.of(job.getTargetNamespace()));
            spec.setTtl("72h0m0s");
            backupResource.setSpec(spec);

            log.info(">>>> CALLING KUBERNETES API (TYPED RESOURCE CREATE)...");

            // 2. Gửi request. Fabric8 sẽ tự động hiểu cách đẩy lên K8s.
            VeleroBackup result = client.resources(VeleroBackup.class)
                    .inNamespace("velero")
                    .resource(backupResource)
                    .create();

            log.info("✅ VELERO BACKUP CREATED! Name: {}, UID: {}",
                    result.getMetadata().getName(),
                    result.getMetadata().getUid());

            job.setStatus(BackupStatus.IN_PROGRESS);
            backupJobRepository.saveAndFlush(job);

            completedServicesMap.remove(jobId);

        } catch (Exception e) {
            log.error("❌ ERROR DURING VELERO CREATION STEP FOR JOB {}: {}", jobId, e.getMessage(), e);
            job.setStatus(BackupStatus.FAILED);
            backupJobRepository.saveAndFlush(job);
        }
    }

    /**
     * Called from Kafka listener when service sends status
     */
    public void handleStatusResponse(UUID jobId, String serviceName, String status) {
        if (!"Đã thành công".equals(status) || !requiredServices.contains(serviceName)) {
            log.warn("Ignoring invalid status from {}: {}", serviceName, status);
            return;
        }

        Set<String> completed = completedServicesMap.computeIfAbsent(jobId,
                k -> ConcurrentHashMap.newKeySet());
        completed.add(serviceName);

        log.info("Job {} {} confirmed ({}). Waiting for: {}", jobId, serviceName, completed.size(),
                requiredServices.stream().filter(s -> !completed.contains(s)).toList());

        if (completed.containsAll(requiredServices)) {
            log.info("🎉 All services confirmed for job {}. Executing Velero!", jobId);
            executeActualVeleroBackup(jobId);
        }
    }

    @KafkaListener(topics = "erp-backup-status")
    public void handleBackupStatus(String message) {
        // Format: "jobId:serviceName:status"
        String[] parts = message.split(":", 3);
        if (parts.length == 3) {
            try {
                UUID jobId = UUID.fromString(parts[0]);
                String serviceName = parts[1];
                String status = parts[2];

                log.info("Kafka status - Job: {}, Service: {}, Status: {}", jobId, serviceName, status);

                handleStatusResponse(jobId, serviceName, status);

            } catch (IllegalArgumentException e) {
                log.warn("Invalid JobID: {}", message);
            }
        } else {
            log.warn("Invalid format: {}", message);
        }
    }

    @Override
    public List<BackupResponseDto> getAllBackups() {
        return backupJobRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BackupResponseDto mapToDto(BackupJob job) {
        return new BackupResponseDto(
                job.getId(),
                job.getVeleroBackupName(),
                job.getTargetNamespace(),
                job.getStatus(),
                job.getCreatedAt());
    }

    @PreDestroy
    public void closeClient() {
        if (this.client != null) {
            this.client.close();
        }
    }

}
