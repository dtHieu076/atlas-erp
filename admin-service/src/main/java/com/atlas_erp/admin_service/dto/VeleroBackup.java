package com.atlas_erp.admin_service.dto;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Version;

// THÊM ĐOẠN ANNOTATION NÀY VÀO ĐÂY LÀ XONG
@Group(value = "velero.io")
@Version(value = "v1")
@Plural("backups")
public class VeleroBackup extends CustomResource<VeleroBackupSpec, VeleroBackupStatus> implements Namespaced {

    // CHÚ Ý: Ghi đè phương thức getKind() để ép nó trả về chữ "Backup"
    @Override
    public String getKind() {
        return "Backup";
    }

    // Các code cũ của bạn bên dưới giữ nguyên...

}