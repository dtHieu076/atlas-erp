# Backup API Implementation TODO

## Plan Steps:
- [x] 1. Update application.properties with PostgreSQL datasource config from provided URI.
- [x] 2. Create enums/BackupStatus.java
- [x] 3. Create entity/BackupJob.java
- [x] 4. Create repository/BackupJobRepository.java
- [x] 5. Create dto/BackupRequestDto.java
- [x] 6. Create dto/BackupResponseDto.java
- [x] 7. Create service/VeleroBackupService.java (interface)
- [x] 8. Create service/impl/VeleroBackupServiceImpl.java
- [x] 9. Create controller/BackupController.java (POST /api/v1/backups + GET /api/v1/backups)
- [ ] 10. Verify: mvn clean compile
- [ ] 11. Test: Run app, POST to trigger backup (needs K8s config), check DB/list API.

Current progress: All code files created successfully. Linter shows package warnings (VSCode issue, ignore). Next: compile and test.

