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

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VeleroBackupServiceImpl implements VeleroBackupService {

    private final BackupJobRepository backupJobRepository;
    private final KubernetesClient client; // 🌟 Tái sử dụng Client

    public VeleroBackupServiceImpl(BackupJobRepository backupJobRepository) {
        this.backupJobRepository = backupJobRepository;
        // Khởi tạo client 1 lần duy nhất khi Service được tạo
        this.client = new KubernetesClientBuilder().build();
    }

    @Override
    @Transactional
    public BackupResponseDto triggerBackup(String targetNamespace) {

        // 1. Khởi tạo tên Backup ngay từ đầu
        String backupName = "manual-backup-" + Instant.now().toEpochMilli();

        // 2. Lưu trạng thái PENDING kèm tên dự kiến để dễ tracking nếu có lỗi
        BackupJob job = new BackupJob(targetNamespace, BackupStatus.PENDING);
        job.setVeleroBackupName(backupName);
        job = backupJobRepository.save(job);

        try {
            // 3. Chuẩn bị "Phiếu gọi món" (Custom Resource)
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

            // 4. Đẩy lệnh lên K8s API Server
            client.genericKubernetesResources("velero.io/v1", "Backup")
                    .inNamespace("velero")
                    .resource(backupCr)
                    .serverSideApply();

            // 5. Nếu không có lỗi, chuyển thành IN_PROGRESS
            // (Việc chuyển thành COMPLETED/FAILED tiếp theo sẽ do lớp Watcher đảm nhận)
            job.setStatus(BackupStatus.IN_PROGRESS);
            backupJobRepository.save(job);

            return mapToDto(job);

        } catch (KubernetesClientException e) {
            // Ghi nhận lỗi vào DB nếu K8s từ chối lệnh
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

    // Đóng kết nối gọn gàng khi ứng dụng tắt
    @PreDestroy
    public void closeClient() {
        if (this.client != null) {
            this.client.close();
        }
    }
}