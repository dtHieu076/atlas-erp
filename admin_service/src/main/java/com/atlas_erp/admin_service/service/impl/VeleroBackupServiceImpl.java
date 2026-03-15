package com.atlas_erp.admin_service.service.impl;

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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class VeleroBackupServiceImpl implements VeleroBackupService {

    private final BackupJobRepository backupJobRepository;

    public VeleroBackupServiceImpl(BackupJobRepository backupJobRepository) {
        this.backupJobRepository = backupJobRepository;
    }

    @Override
    public BackupResponseDto triggerBackup(String targetNamespace) {

        BackupJob job = new BackupJob(targetNamespace, BackupStatus.PENDING);
        backupJobRepository.save(job);

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            String backupName = "manual-backup-" + Instant.now().toEpochMilli();

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

            client.resources(GenericKubernetesResource.class)
                    .inNamespace("velero")
                    .resource(backupCr)
                    .serverSideApply();

            job.setVeleroBackupName(backupName);
            job.setStatus(BackupStatus.IN_PROGRESS);
            backupJobRepository.save(job);

            return mapToDto(job);

        } catch (KubernetesClientException e) {

            job.setStatus(BackupStatus.FAILED);
            backupJobRepository.save(job);

            throw new RuntimeException(
                    "Failed to trigger Velero backup for namespace: " + targetNamespace, e);
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
}