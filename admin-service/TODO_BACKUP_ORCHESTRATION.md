# KẾ HOẠCH TRIỂN KHAI BACKUP BẤT ĐỒNG BỘ (Kafka + Velero Orchestration)
## Đánh giá tổng thể: 8/10 - APPROVED với điều chỉnh

### THÔNG TIN GATHERED TỪ CODEBASE:
- ✅ Có BackupJob entity, Repository, DTOs
- ✅ Có BackupKafkaService với topics `erp-backup-commands`, `erp-backup-status`
- ✅ Có VeleroBackupServiceImpl (hiện sync, cần refactor async)
- ✅ Controller có test endpoint
- ❌ Thiếu: Logic đếm service confirm, Job ID trong Kafka msg, config required-services

### PLAN CHI TIẾT (File-level):

**1. admin-service/src/main/resources/application.properties**
```
+ backup.required-services=hr-service,organization-service,workflow-service
```

**2. admin-service/src/main/java/com/atlas_erp/admin_service/service/impl/VeleroBackupServiceImpl.java**
```
- Refactor triggerBackup(): Tạo Job → sendBackupCommand(jobId) → return WAITING
+ Thêm executeActualVeleroBackup(UUID jobId)
+ Thêm handleStatusResponse(UUID jobId, String service, String status)
+ ConcurrentHashMap<UUID, Set<String>> tracking
+ @Autowired BackupKafkaService
```

**3. admin-service/src/main/java/com/atlas_erp/admin_service/event/BackupKafkaService.java**
```
+ sendBackupCommand(UUID jobId): kafka.send(COMMANDS_TOPIC, jobId.toString() + \":Cần backup\")
- @KafkaListener: Parse \"jobId:service:status\" → veleroService.handleStatusResponse(...)
```

**4. admin-service/src/main/java/com/atlas_erp/admin_service/controller/BackupController.java** (optional)
```
+ BackupJobService để tạo job trước
```

### DEPENDENT FILES:
- BackupJob.java (thêm status WAITING_FOR_SERVICES nếu cần)
- BackupStatus enum (thêm WAITING_FOR_SERVICES, CONFIRMED_SERVICES)

### FOLLOW-UP STEPS:
1. Implement theo thứ tự 1→4
2. Test: POST /api/v1/backups → Check Kafka msg → Manual simulate status → Verify Velero CR
3. Deploy + monitor logs
4. Cấu hình PV/annotations cho services (hr/org/workflow) để Velero backup /app/backups

**STATUS
