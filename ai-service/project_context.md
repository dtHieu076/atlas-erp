# Atlas ERP AI Service - Project Context

## 1. Project Overview & Purpose
AI microservice for **Atlas ERP Wood Processing Platform**. Core domain: **HR Data Intelligence + Document Processing**.

- **Primary functions**:
  - **Tool-Calling LLM Chat**: Natural language queries on HR data (employees, attendance) via Postgres.
  - **Vietnamese ID Card OCR**: Extract/parse CCCD data from front/back images using OCR + LLM Vision.
- Business value: Automate HR reporting/analytics and employee onboarding via AI.

Monolith REST API, stateless, read-only HR queries.

## 2. Tech Stack & Infrastructure
| Component | Tech | Version/Notes |
|-----------|------|---------------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.4.0 |
| ORM | Spring Data JPA + Hibernate | Postgres Dialect |
| DB | PostgreSQL | Neon hosted (`hr_db`) |
| AI/LLM | Spring AI | 1.0.0-M6 + OpenRouter (StepFun/Qwen models) |
| External APIs | OCR.space (fallback), Python OCR Service (localhost:8000) | Image-to-text |
| HTTP Client | WebClient (reactive) | OpenRouter API calls |
| Build | Maven | Wrapper (`mvnw`) |
| Utils | Lombok, Jackson, WebFlux | DTOs, JSON |

**No caching/brokers/vector stores active** (PGVector dep present but unused).

## 3. High-Level Directory Structure
```
ai-service/
├── src/main/java/com/atlas_erp/ai_service/
│   ├── AiServiceApplication.java     # Spring Boot entrypoint
│   ├── Controller/                   # REST endpoints (AiChatController, IdCardController)
│   ├── Dto/Request/                  # API input DTOs (ChatRequest, IdCardRequest)
│   ├── Dto/Response/                 # API output DTOs + ApiResponse wrapper
│   ├── Entity/                       # JPA entities (EmployeeEntity, AttendanceEntity)
│   ├── Repository/                   # Spring Data JPA repos
│   ├── Service/Impl/                 # Business logic (ToolCallChatServiceImpl, IdCardServiceImpl)
│   ├── Client/                       # External API clients (OcrClient)
│   ├── tools/                        # LLM tools (HrTools)
│   ├── enums/                        # EmploymentStatus, AttendanceStatus, GenderType
│   └── prompt/                       # System prompts + DB schema (DatabaseSchemaProvider)
├── src/main/resources/
│   └── application.properties        # Config (DB, OpenRouter, OCR)
├── pom.xml                           # Dependencies
└── Dockerfiles/TODOs                 # Deployment/roadmap files
```

**Folder conventions**: Standard Spring Boot MVC → **Controller** (HTTP), **Service/Impl** (orchestration/tools), **Repository** (DAO), **Dto** (API contracts).

## 4. Architecture & Design Patterns
- **Pattern**: **Layered MVC** (Controller → Service → Repository) + **Tool-Calling Agent** (custom loop in ToolCallChatServiceImpl).
- **Key conventions**:
  - **Response wrapper**: All APIs return `ApiResponse<T>` (`success: boolean, data: T, message: string`).
  - **Error handling**: `@Slf4j` + try-catch → `ResponseEntity.badRequest/internalServerError(ApiResponse.error())`.
  - **Stateless**: No session/history storage; `sessionId` DTO field for client-side tracking.
  - **Security**: HR tools **read-only** (SQL whitelist: SELECT only, no DML).
  - **LLM integration**: Direct WebClient to OpenRouter (bypasses Spring AI limitations).

**Agent loop**: LLM → tool_calls → execute (reflection) → assistant msg → repeat (max 10).

## 5. Data Models / Database Schema
**DB**: `hr_db` (Postgres) - **Singular table names** (`employee`, `attendance`).

### employee
| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK | |
| employee_code | VARCHAR | UNIQUE, NOT NULL | |
| full_name | VARCHAR | NOT NULL | |
| gender | VARCHAR | GenderType enum | |
| dob | DATE | | |
| national_id | VARCHAR | | CCCD |
| phone | VARCHAR | | |
| work_email | VARCHAR | | |
| permanent_address | TEXT | | |
| hire_date | DATE | | |
| status | VARCHAR | EmploymentStatus enum | ACTIVE/PROBATION/TERMINATED |

### attendance
| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK | |
| employee_id | UUID | FK (employee.id), NOT NULL | 1-n relationship |
| work_date | DATE | NOT NULL | |
| check_in/out | TIMESTAMP | | |
| worked_hours | DECIMAL | | |
| status | VARCHAR | AttendanceStatus enum | WORKING/ABSENT/LATE |

**Relationships**: `employee 1-n attendance` (via `employee_id`).

**Indexes/Constraints**: Standard JPA defaults; no custom noted.

## 6. Core Business Flows
1. **HR Chat Query** (`POST /api/ai/chat`):
   ```
   Client → AiChatController.chat(ChatRequest) 
   → ToolCallChatServiceImpl.agentLoop() 
   → LLM (OpenRouter) + HrTools (JPA/SQL) 
   → ChatResponse.answer
   ```
   *Why*: LLM decides tools (e.g., `get_today_attendance` → repo → JSON).

2. **ID Card OCR** (`POST /api/ai/ocr/id-card`):
   ```
   Multipart Images → IdCardController.processIdCard()
   → OcrService.extractText() (Python/OCR.space)
   → ParserService (regex) + LLM.parseRawOcr()
   → IdCardResponse.data (IdCardData)
   ```
   *Why*: Hybrid OCR + LLM for VN CCCD accuracy.

3. **Direct Vision LLM** (`POST /api/ai/ocr/id-card-llm`):
   ```
   Base64 Image → IdCardServiceImpl.processIdCardLlm()
   → Vision LLM (Qwen2.5-VL) → JSON parse → IdCardData
   ```

## 7. API Contracts & DTOs
**Base wrapper**: `ApiResponse<T>` `{success: bool, data: T, message: string}`

| Endpoint | Method | DTO In | DTO Out | Summary |
|----------|--------|--------|---------|---------|
| `/api/ai/chat` | POST | `ChatRequest` `{question: str, sessionId?: str, context?: str}` | `ChatResponse` `{answer: str, sessionId: str, model: str}` | LLM HR chat w/ tools |
| `/api/ai/ocr/id-card` | POST | Multipart `frontImage`, `backImage` | `IdCardResponse` → `IdCardData` (10+ fields: fullName, idNumber, ...) | Dual-side CCCD OCR |
| `/api/ai/ocr/id-card-llm` | POST | Multipart `frontImage` (base64) | `IdCardResponse` → `IdCardData` | Vision LLM single image |
| `*/health` | GET | - | `{status: 'UP'}` | Health checks |

**Key DTO example** (`IdCardData`):
```json
{
  "fullName": "NGUYEN VAN A",
  "idNumber": "123456789012",
  "dateOfBirth": "01/01/1990",
  "formattedText": "Thông tin nhân viên: NGUYEN VAN A, ..."
}
```

## 8. Configuration & Environment
| Key | Placeholder/Example | Purpose |
|-----|---------------------|---------|
| `spring.datasource.*` | `jdbc:postgresql://<neon-host>/hr_db?sslmode=require`<br>`<db-user>`, `<db-pass>` | Postgres connection |
| `spring.ai.openai.*` | `https://openrouter.ai/api`<br>`sk-or-v1-<key>`<br>`stepfun/step-3.5-flash:free` | LLM API |
| `ocr.service.*` | `http://localhost:8000`<br>`timeout=120s` | Python OCR microservice |
| `ai-service.vision.llm.model` | `qwen/qwen-2.5-vl-3b-instruct:free` | Vision model |

**Run**: `mvn spring-boot:run` (expects DB populated w/ demo HR data).

