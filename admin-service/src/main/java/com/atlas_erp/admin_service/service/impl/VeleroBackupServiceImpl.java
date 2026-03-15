package com.atlas_erp.admin_service.service.impl;

import com.atlas_erp.admin_service.service.BackupKafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.atlas_erp.admin_service.dto.BackupResponseDto;
import com.atlas_erp.admin_service.entity.BackupJob;
import com.atlas_erp.admin_service.enums.BackupStatus;
import com.atlas_erp.admin_service.repository.BackupJobRepository;
import com.atlas_erp.admin_service.service.VeleroBackupService;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VeleroBackupServiceImpl implements VeleroBackupService {

    // 1. Chỉ khai báo mỗi biến một lần duy nhất
    private final BackupJobRepository backupJobRepository;
    private final BackupKafkaService backupKafkaService;
    private final KubernetesClient client;

    // Track pending backup responses
    private final Map<Long, Set<String>> pendingResponses = new ConcurrentHashMap<>();

    private final Set<String> requiredServices;

    // 2. Constructor duy nhất để khởi tạo tất cả các trường final
    public VeleroBackupServiceImpl(
            BackupJobRepository backupJobRepository,
            BackupKafkaService backupKafkaService,
            @Value("${backup.required-services:}") String requiredServicesStr) {

        this.backupJobRepository = backupJobRepository;
        this.backupKafkaService = backupKafkaService;

        // Khởi tạo Kubernetes Client (chỉ 1 lần)
        this.client = new KubernetesClientBuilder().build();

        // Khởi tạo Set các service bắt buộc từ chuỗi config
        if (requiredServicesStr != null && !requiredServicesStr.trim().isEmpty()) {
            this.requiredServices = new HashSet<>(Arrays.asList(requiredServicesStr.split(",")));
        } else {
            this.requiredServices = new HashSet<>();
        }

        log.info("Initialized backup tracking for services: {}", requiredServices);
    }

    /**
     * Kafka listener for backup status responses
     */
    @KafkaListener(topics = "erp-backup-status")
    public void handleBackupStatus(String message) {
        // Expected format: "hr-service:Đã thành công"
        String[] parts = message.split(":", 2);
        if (parts.length == 2) {
            String serviceName = parts[0].trim();
            String status = parts[1].trim();

            // Gọi service trung gian xử lý logic Kafka
            backupKafkaService.handleStatusResponse(serviceName, status);
            log.info("Received backup status from {}: {}", serviceName, status);

            if (requiredServices.contains(serviceName) && "Đã thành công".equals(status)) {
                log.info("✅ Backup thành công - Required service {} confirmed!", serviceName);
            }
        } else {
            log.warn("Invalid status message format: {}", message);
        }
    }

    @Override
    @Transactional
    public BackupResponseDto triggerBackup(String targetNamespace) {
        String backupName = "manual-backup-" + Instant.now().toEpochMilli();

        BackupJob job = new BackupJob(targetNamespace, BackupStatus.PENDING);
        job.setVeleroBackupName(backupName);
        job = backupJobRepository.save(job);

        try {
            GenericKubernetesResource backupCr = new GenericKubernetesResource();
            backupCr.setApiVersion("velero.io/v1");
            backupCr.setKind("Backup");

            backupCr.setMetadata(new ObjectMetaBuilder()
                    .withName(backupName)
                    .withNamespace("velero")
                    .build());

            Map<String, Object> spec = new HashMap<>();
            spec.put("includedNamespaces", List.of(targetNamespace));
            spec.put("ttl", "72h0m0s");

            backupCr.getAdditionalProperties().put("spec", spec);

            client.genericKubernetesResources("velero.io/v1", "Backup")
                    .inNamespace("velero")
                    .resource(backupCr)
                    .serverSideApply();

            job.setStatus(BackupStatus.IN_PROGRESS);
            backupJobRepository.save(job);

            return mapToDto(job);

        } catch (KubernetesClientException e) {
            job.setStatus(BackupStatus.FAILED);
            backupJobRepository.save(job);
            throw new RuntimeException("Failed to trigger Velero backup: " + targetNamespace, e);
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