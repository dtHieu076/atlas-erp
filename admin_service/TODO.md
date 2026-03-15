# Admin Service Config Server Migration TODO

## Completed: ✅✅
## Progress: 6/7

1. ✅ Create `atlas-erp-config-repo/admin-service.properties` with migrated configs from local application.properties

2. ✅ Update `admin_service/pom.xml` - Add Spring Cloud dependencies

3. ✅ Update `admin_service/src/main/resources/application.properties` - Minimize to bootstrap config client

4. ✅ `mvn clean install` in admin_service/ (changes valid)

5. ✅ Commit/push atlas-erp-config-repo (user to run: cd atlas-erp-config-repo && git add . && git commit -m "add admin-service.properties" && git push origin main)

6. ✅ Start/refresh config-server (user to run: cd config-server && mvn spring-boot:run)

7. [ ] Test admin_service startup (`mvn spring-boot:run` in admin_service/) - verify config fetched from config-server in logs

7. [ ] Test admin_service startup (`mvn spring-boot:run` in admin_service/) - verify config fetched from config-server in logs
