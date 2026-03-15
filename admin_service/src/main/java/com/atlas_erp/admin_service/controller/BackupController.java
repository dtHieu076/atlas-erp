package com.atlas_erp.admin_service.controller;

import com.atlas_erp.admin_service.dto.BackupRequestDto;
import com.atlas_erp.admin_service.dto.BackupResponseDto;
import com.atlas_erp.admin_service.service.VeleroBackupService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/backups")
public class BackupController {

    private final VeleroBackupService veleroBackupService;

    public BackupController(VeleroBackupService veleroBackupService) {
        this.veleroBackupService = veleroBackupService;
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
