# Agent Loop Flow - Custom Tool Calling với StepFun

## Tổng quan

Triển khai custom agent loop để giải quyết vấn đề StepFun provider không hỗ trợ `role: tool` trong conversation history.

## Luồng hoạt động

```
User Request
    ↓
AiChatController
    ↓
ToolCallChatService.chatWithTools()
    ↓
┌─────────────────────────────────────────┐
│ 1. Build System Prompt                  │
│    - Role: AI assistant for HR system    │
│    - Available tools (6 tools)           │
│    - Database schema                     │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 2. Custom Agent Loop                    │
│    (max 10 iterations)                  │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ Request → OpenRouter API                 │
│                                          │
│ {                                        │
│   "model": "stepfun/step-3.5-flash",    │
│   "messages": [...],                     │
│   "tools": [...]                         │
│ }                                        │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ Response từ LLM                          │
│                                          │
│ Có tool_calls?                          │
│   → YES: Execute tool                   │
│   → NO: Trả response cuối cùng          │
└─────────────────────────────────────────┘
    ↓
[NẾU CÓ TOOL_CALLS]
┌─────────────────────────────────────────┐
│ 3. Execute Tool                          │
│    executeTool(name, arguments)         │
│    → HrTools.method(sql)                │
│    → Database query                     │
│    → Return JSON result                 │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 4. Thêm Tool Result vào Messages        │
│                                          │
│ QUAN TRỌNG: Dùng role="assistant"      │
│ thay vì role="tool" (StepFun không      │
│ hỗ trợ role="tool")                     │
│                                          │
│ {                                       │
│   "role": "assistant",                  │
│   "content": "Tool query_hr_database    │
│               returned: [...]"            │
│ }                                       │
└─────────────────────────────────────────┘
    ↓
[QUAY LẠI BƯỚC Request → OpenRouter]
    ↓
[CHO ĐẾN KHI KHÔNG CÒN TOOL_CALLS]
    ↓
Trả response cho user
```

## System Prompt

```java
buildSystemPrompt() = 
  """
  Bạn là trợ lý AI cho hệ thống quản lý nhân sự Atlas ERP.

  Nhiệm vụ của bạn:
  1. Hiểu câu hỏi của người dùng về dữ liệu nhân sự
  2. Sử dụng các tools được cung cấp để truy vấn database khi cần thiết
  3. Tổng hợp kết quả và trả lời bằng tiếng Việt

  Các tools có sẵn:
  - query_hr_database: Thực thi câu SQL (SELECT) để lấy dữ liệu
  - search_employees: Tìm kiếm nhân viên theo tên
  - get_today_attendance: Lấy danh sách chấm công hôm nay
  - get_employee_list: Lấy danh sách nhân viên
  - get_employee_statistics: Lấy thống kê nhân viên
  - get_attendance_by_date: Lấy chấm công theo ngày cụ thể

  Nguyên tắc trả lời:
  - Khi người dùng hỏi về số liệu, hãy gọi tool để lấy dữ liệu chính xác
  - Trả lời ngắn gọn, rõ ràng
  - Nếu không tìm thấy dữ liệu, nói rõ "Không tìm thấy dữ liệu..."
  - Nếu câu hỏi không liên quan đến HR, trả lời dựa trên kiến thức chung

  """ + DatabaseSchemaProvider.getHrDatabaseSchema()
```

## Ví dụ Request/Response

### Request 1
```json
{
  "model": "stepfun/step-3.5-flash:free",
  "messages": [
    {
      "role": "system",
      "content": "Bạn là trợ lý AI..."
    },
    {
      "role": "user",
      "content": "Có bao nhiêu nhân viên nữ?"
    }
  ],
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "query_hr_database",
        "description": "Thực thi câu truy vấn SQL...",
        "parameters": {
          "type": "object",
          "properties": {
            "sql": {"type": "string", "description": "Câu truy vấn SQL"}
          },
          "required": ["sql"]
        }
      }
    },
    // ... other tools
  ]
}
```

### Response 1 (LLM gọi tool)
```json
{
  "choices": [{
    "message": {
      "role": "assistant",
      "content": "",
      "tool_calls": [{
        "id": "call_abc123",
        "type": "function",
        "function": {
          "name": "query_hr_database",
          "arguments": "{\"sql\": \"SELECT COUNT(*) FROM employee WHERE gender = 'FEMALE'\"}"
        }
      }]
    }
  }]
}
```

### Execute Tool
```java
// Tìm method trong HrTools
String methodName = convertToCamelCase("query_hr_database"); // → "queryHrDatabase"

// Gọi method
HrTools hrTools = ...;
Method method = hrTools.getClass().getDeclaredMethod("queryHrDatabase", String.class);
String result = (String) method.invoke(hrTools, sql);

// result = "[{\"count\": 5}]"
```

### Thêm Tool Result vào Messages
**CÁCH CŨ (Spring AI - StepFun KHÔNG hiểu):**
```json
{
  "role": "tool",
  "tool_call_id": "call_abc123",
  "name": "query_hr_database",
  "content": "[{\"count\": 5}]"
}
```

**CÁCH MỚI (Custom - StepFun HIỂU):**
```json
{
  "role": "assistant",
  "content": "Tool query_hr_database returned: [{\"count\": 5}]"
}
```

### Request 2 (với tool result)
```json
{
  "model": "stepfun/step-3.5-flash:free",
  "messages": [
    {"role": "system", "content": "..."},
    {"role": "user", "content": "Có bao nhiêu nhân viên nữ?"},
    {
      "role": "assistant",
      "tool_calls": [{
        "id": "call_abc123",
        "function": {"name": "query_hr_database", "arguments": "..."}
      }]
    },
    {
      "role": "assistant",
      "content": "Tool query_hr_database returned: [{\"count\": 5}]"
    }
  ]
}
```

### Response cuối cùng
```json
{
  "choices": [{
    "message": {
      "role": "assistant",
      "content": "Hiện tại hệ thống có 5 nhân viên nữ."
    }
  }]
}
```

## Code chính

### ToolCallChatServiceImpl.java

```java
@Slf4j
@Service
public class ToolCallChatServiceImpl implements ToolCallChatService {

    private String executeAgentLoop(List<Map<String, Object>> messages) {
        int maxIterations = 10;
        
        while (iteration < maxIterations) {
            // 1. Gọi LLM
            String response = callLLM(messages);
            
            // 2. Parse response
            JsonNode toolCalls = response.get("tool_calls");
            
            // 3. Thêm assistant message vào history
            messages.add(assistantMsg);
            
            // 4. Nếu không có tool_calls → return
            if (toolCalls == null) return content;
            
            // 5. Execute tools
            for (toolCall : toolCalls) {
                String result = executeTool(name, args);
                
                // 6. Thêm result dưới dạng assistant message
                messages.add(Map.of(
                    "role", "assistant",
                    "content", "Tool " + name + " returned: " + result
                ));
            }
        }
    }
}
```

## Điểm quan trọng

1. **WebClient trực tiếp** - Không dùng Spring AI ChatClient
2. **Custom agent loop** - Tự xử lý tool calling thay vì để Spring AI xử lý
3. **Assistant message thay cho tool message** - Giải pháp cho StepFun
4. **Snake_case → camelCase** - Map tool names với method names
5. **Reflection** - Execute methods trong HrTools bean

## Tools available

| Tool Name | Method | Parameters |
|-----------|--------|------------|
| query_hr_database | queryHrDatabase | sql |
| search_employees | searchEmployees | name |
| get_today_attendance | getTodayAttendance | - |
| get_employee_list | getEmployeeList | limit |
| get_employee_statistics | getEmployeeStatistics | - |
| get_attendance_by_date | getAttendanceByDate | date |

