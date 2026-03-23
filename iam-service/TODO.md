# TODO: Refactor /auth/login to Google ID Token Verification

## Approved Plan Steps

### Phase 1: Dependencies & Core Entities/DTOs (Start here)
- [ ] **Step 1**: Edit `pom.xml` - Add Google auth library
- [ ] **Step 2**: Update `UserIdentityEntity.java` - Simplify to googleId only
- [ ] **Step 3**: Update `LoginRequest.java` - Single credential field
- [ ] **Step 4**: Update `UserIdentityRepository.java` - Add findByGoogleId()
- [ ] **Step 5**: Update `CachedUserIdentity.java` - Remove provider, rename to googleId

### Phase 2: Cache Layer
- [x] **Step 6**: Refactor `UserIdentityCacheService.java` - googleId keys only

### Phase 3: Security Services
- [x] **Step 7**: **CREATE** `GoogleTokenVerifier.java` - ID token verification
- [ ] **Step 8**: Update `JwtTokenService.java/impl.java` - Add sessionId support

### Phase 4: Response & Main Logic
- [ ] **Step 9**: Update `LoginResponse.java` - (No change, per approval)
- [x] **Step 10**: Refactor `AuthServiceImpl.java` - New 7-step flow
- [x] **Step 11**: Add config to `application.properties` - google.client-id

### Phase 5: Validation & Test
- [ ] Run `mvn clean compile`
- [ ] Manual test /auth/login endpoint
- [ ] Update DB schema (ALTER TABLE)

**Current Progress: Phase 1-2 complete, fixing deps/compilation → Phase 3**

**Next:** JWT sessionId updates (Step 8) after successful compile

