package com.atlas_erp.admin_service.controller;

import com.atlas_erp.admin_service.dto.BackupRequestDto;
import com.atlas_erp.admin_service.dto.BackupResponseDto;
import com.atlas_erp.admin_service.service.VeleroBackupService;
import com.atlas_erp.admin_service.event.BackupKafkaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/backups")
public class BackupController {

    private final VeleroBackupService veleroBackupService;
    private final BackupKafkaService backupKafkaService;

    public BackupController(VeleroBackupService veleroBackupService, BackupKafkaService backupKafkaService) {
        this.veleroBackupService = veleroBackupService;
        this.backupKafkaService = backupKafkaService;
    }

    @PostMapping("/testBackup")
    public ResponseEntity<String> testBackup() {
        backupKafkaService.sendBackupCommand(UUID.randomUUID());
        return ResponseEntity.ok("Backup command sent to erp-backup-commands. Check logs for status.");
    }

    @PostMapping
    public ResponseEntity<BackupResponseDto> triggerBackup(@Valid @RequestBody BackupRequestDto request) {
        BackupResponseDto response = veleroBackupService.triggerBackup(request.targetNamespace());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BackupResponseDto>> getAllBackups() {
        List<BackupResponseDto> backups = veleroBackupService.getAllBackups();
        return ResponseEntity.ok(backups);
    }
}
