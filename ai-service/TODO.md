# Wood ERP AI Service Refactoring: Spring AI Gemini Tool Calling + Streaming

## Status: 🚀 In Progress

### Step 1: [x] Update Dependencies (pom.xml)
- Replace `spring-ai-openai-spring-boot-starter` with `spring-ai-gemini-spring-boot-starter`
- Run `mvn clean compile` *(artifact issue - using OpenAI starter with Gemini model fallback)*

### Step 2: [x] Update Configuration (application.properties)
- Add Gemini config: `spring.ai.gemini.chat.options.model=gemini-2.5-flash`
- Remove/update OpenRouter props *(placeholder key)*

### Step 3: [x] Create HrToolConfig.java
- Configuration class with 6 `@Bean Function<DTO, String>`
- DTO records for each tool param

### Step 4: [x] Update Interface ToolCallChatService.java
- Change to `Flux<String> chatWithToolsStream(ChatRequest)`

### Step 5: [x] Refactor ToolCallChatServiceImpl.java
- Remove custom loop, WebClient, Reflection
- Use ChatClient fluent API with `.functions()` and `.stream().content()`

### Step 6: [x] Update AiChatController.java
- `/chat` endpoint to SSE `Flux<ServerSentEvent<String>>`
- `produces = MediaType.TEXT_EVENT_STREAM_VALUE`

### Step 7: [ ] Test
- `mvn spring-boot:run`
- `curl -X POST http://localhost:8080/api/ai/chat -H "Content-Type: application/json" -d '{"question":"Số nhân viên hôm nay"}'`
- Verify streaming response with tool calls

### Later: [ ] Frontend Integration
- Update to handle SSE stream
- Typing effect animation

**Completed Steps: None yet**
**Next Step: Update pom.xml**

