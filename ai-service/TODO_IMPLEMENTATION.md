# Implementation Plan: Custom Agent Loop for StepFun Provider

## Problem
StepFun provider doesn't support `role: tool` in conversation history. When Spring AI sends the second request with tool result, it uses role "tool" which StepFun rejects.

## Solution
Implement custom agent loop manually using WebClient directly instead of Spring AI's ChatClient.

## Files to Modify

### 1. ToolCallChatServiceImpl.java
**Changes:**
- Remove ChatClient dependency
- Use WebClient directly to call OpenRouter API
- Implement custom agent loop with proper message format

**Key Changes:**
1. Build tools JSON schema in OpenAI format for StepFun
2. Send initial request to LLM
3. If response has tool_calls:
   - Execute tool via reflection
   - Convert tool result to assistant message (NOT tool role)
   - Send follow-up request with updated conversation
   - Loop until no more tool_calls

**Message Format:**
```json
// OLD (not supported by StepFun):
{
  "role": "tool",
  "tool_call_id": "call_xxx",
  "name": "query_hr_database", 
  "content": "[{\"count\":2}]"
}

// NEW (supported by StepFun):
{
  "role": "assistant",
  "content": "Tool query_hr_database returned: [{\"count\":2}]"
}
```

## Implementation Steps

1. Add Jackson ObjectMapper for JSON processing
2. Create tools schema in OpenAI function calling format
3. Implement custom agent loop:
   - callLLM(): Send request to OpenRouter
   - executeTool(): Execute tool by name
   - buildMessages(): Build message list with proper format
4. Replace chatWithTools() method

## Tools Available
- query_hr_database
- search_employees
- get_today_attendance
- get_employee_list
- get_employee_statistics
- get_attendance_by_date

