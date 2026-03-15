# Kafka Backup Test Implementation Plan

Status: ✅ Approved by user

## Steps to Complete:

### 1. **Create BackupKafkaService in admin_service** 📝
- Path: `admin_service/src/main/java/com/atlas_erp/admin_service/service/BackupKafkaService.java`
- Inject KafkaTemplate<String, String>, methods: sendBackupCommand(), handleStatusResponse(String)

### 2. **Update VeleroBackupServiceImpl in admin_service** 🔄
- Path: `admin_service/src/main/java/com/atlas_erp/admin_service/service/impl/VeleroBackupServiceImpl.java`
- Add @KafkaListener for erp-backup-status
- Add tracking logic (Set<String>), @Value for backup.required-services
- Log "Backup thành công" when hr-service responds

### 3. **Update BackupController in admin_service** 🔄
- Path: `admin_service/src/main/java/com/atlas_erp/admin_service/controller/BackupController.java`
- Add @PostMapping("/testBackup"), call sendBackupCommand()

### 4. **Create BackupEventService in hr-service** 📝
- Path: `hr-service/src/main/java/com/atlas_erp/hr_service/Event/serviceEvent/BackupEventService.java`
- @KafkaListener erp-backup-commands → produce "Đã thành công" to erp-backup-status

### 5. **Compile & Test** 🧪
```
cd admin_service &amp;&amp; mvn clean compile
cd ../hr-service &amp;&amp; mvn clean compile
```
- Restart services
- curl -X POST http://localhost:8080/api/v1/backups/testBackup
- Check logs

### 6. **Cleanup/Remove after test** 🗑️ (optional)

**Progress: 4/6 completed** ✅

- [x] 1. Create BackupKafkaService 
- [x] 2. Update VeleroBackupServiceImpl  
- [x] 3. Update BackupController
- [x] 4. Create BackupEventService

**Next step: 5. Compile & Test**

