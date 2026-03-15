# Streaming Implementation TODO

## Completed
- [x] Create TODO.md

## Step 1: Update Service Interface
- [x] Add `Flux<String> chatStream(ChatRequest request);` to ToolCallChatService

## Step 2: Implement Streaming in Service Impl
- [x] Add `callLLMStream()` -> Flux<String> with `"stream": true`
- [x] Add `extractContent()` parser
- [x] Implement `chatStream()`: run blocking agent loop, stream final response

## Step 3: Update Controller
- [x] Add `@PostMapping("/chat/stream")` SSE endpoint

## Step 3: Update Controller
- Add `@PostMapping("/chat/stream")` SSE endpoint

## Step 4: Test
- Test blocking `/chat`
- Test streaming `/chat/stream`
- Verify tool calling works in streaming mode

## Step 5: Advanced (Future)
- Full streaming agent loop (stream reasoning + tool calls)
- Frontend SSE client integration
