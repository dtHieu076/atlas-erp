# AI Service Implementation TODO

## Completed Tasks ✅

### Phase 1: Basic AI Chat
1. ✅ Create DTOs
   - `Dto/Request/ChatRequest.java` - Request body for chat endpoint
   - `Dto/Response/ApiResponse.java` - Generic API response wrapper
   - `Dto/Response/ChatResponse.java` - Response for chat endpoint

2. ✅ Create Service Layer (Multi-layer Architecture)
   - `Service/ChatService.java` - Interface (abstract layer)
   - `Service/Impl/ChatServiceImpl.java` - Implementation layer

3. ✅ Create Controller Layer
   - `Controller/AiChatController.java` - REST API endpoints

4. ✅ Create Config
   - `Config/CorsConfig.java` - CORS configuration

### Phase 2: Database Integration (RAG)
5. ✅ Create Entity Layer
   - `Entity/EmployeeEntity.java` - Employee entity
   - `Entity/AttendanceEntity.java` - Attendance entity
   - `Entity/LeaveRequestEntity.java` - Leave request entity

6. ✅ Create Enums
   - `enums/EmploymentStatus.java`
   - `enums/AttendanceStatus.java`
   - `enums/GenderType.java`

7. ✅ Create Repository Layer
   - `Repository/EmployeeRepository.java`
   - `Repository/AttendanceRepository.java`
   - `Repository/LeaveRequestRepository.java`

8. ✅ Create DataReader Module
   - `DataReader/DatabaseReader.java` - Interface
   - `DataReader/Impl/SqlDatabaseReader.java` - SQL implementation

9. ✅ Create QueryProcessor Module
   - `QueryProcessor/IntentClassifier.java` - Intent classification

10. ✅ Create ContextBuilder Module
    - `ContextBuilder/PromptContextBuilder.java` - Build context for LLM

11. ✅ Create RAG Service
    - `Service/ChatRagService.java` - Interface
    - `Service/Impl/ChatRagServiceImpl.java` - Implementation

12. ✅ Update Controller with new endpoints
    - `Controller/AiChatController.java` - Added `/chat/database` and `/chat/auto`

13. ✅ Configure Database Connection (Static - Demo)
    - `application.properties` - HR Database connection

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ai/chat` | Chat with AI (general - no DB) |
| POST | `/api/ai/chat/database` | Chat with HR Database context |
| POST | `/api/ai/chat/auto` | Auto mode (DB → General if fails) |
| GET | `/api/ai/health` | Health check |

## Request/Response Examples

### POST /api/ai/chat (General)
**Request:**
```json
{
  "question": "Hello, how are you?",
  "sessionId": "optional-session-id",
  "context": "optional context"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Chat processed successfully",
  "data": {
    "answer": "AI response here",
    "sessionId": "session-id-or-generated-uuid",
    "timestamp": 1234567890,
    "model": "stepfun/step-3.5-flash:free"
  },
  "timestamp": 1234567890
}
```

### POST /api/ai/chat/database (RAG)
**Request:**
```json
{
  "question": "Có bao nhiêu nhân viên đang làm việc?",
  "sessionId": "optional-session-id"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Database chat processed successfully",
  "data": {
    "answer": "Hiện tại hệ thống có 150 nhân viên đang làm việc...",
    "sessionId": "session-id",
    "timestamp": 1234567890,
    "model": "stepfun/step-3.5-flash:free"
  }
}
```

## Supported Questions (Demo)

The AI can answer these HR-related questions:
- "Có bao nhiêu nhân viên đang làm việc?" → Employee count
- "Danh sách nhân viên" → Employee list
- "Chấm công hôm nay" → Today's attendance
- "Có bao nhiêu người nghỉ phép hôm nay?" → Leave requests
- "Đơn nghỉ phép chờ duyệt" → Pending leave requests
- And more general questions...

## Next Steps

1. **Run the application**: `cd ai-service && mvn spring-boot:run`
2. **Test the API endpoints** using Postman or curl
3. **Add more features**:
   - Cache layer for frequently asked questions
   - Support more database tables (Inventory, Organization)
   - VectorStore for semantic search
   - Error handling improvements
   - Unit tests

## Database Configuration (Static - Demo)

```properties
# HR Database Connection
spring.datasource.url=jdbc:postgresql://ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/hr_db?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_ODZx4pvMKCU1
```

## Architecture Overview

```
User Query
    ↓
┌─────────────────────────────────────────┐
│  AiChatController                       │
│  - /chat (General LLM)                  │
│  - /chat/database (RAG)                 │
│  - /chat/auto (Auto mode)               │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  IntentClassifier                       │
│  - Classify: EMPLOYEE, ATTENDANCE,      │
│    LEAVE, CONTRACT, GENERAL            │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  SqlDatabaseReader / ApiDatabaseReader  │
│  - Query HR Database                    │
│  - Return structured data                │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  PromptContextBuilder                   │
│  - Build context from DB data            │
│  - Create full prompt for LLM            │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  ChatRagService / ChatService           │
│  - Call LLM with context                │
│  - Generate final response              │
└─────────────────────────────────────────┘
    ↓
Response to User
```


