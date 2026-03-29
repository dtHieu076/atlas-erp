package com.atlas_erp.admin_service.service;

import com.atlas_erp.admin_service.dto.BackupResponseDto;
import com.atlas_erp.admin_service.dto.VeleroBackupItemDto;

import java.util.List;

public interface VeleroBackupService {
    BackupResponseDto triggerBackup(String targetNamespace);

    List<BackupResponseDto> getAllBackups();

    List<VeleroBackupItemDto> getRealTimeVeleroBackups();

    VeleroBackupItemDto getVeleroBackupDetail(String name);

    boolean deleteVeleroBackup(String name);
}
