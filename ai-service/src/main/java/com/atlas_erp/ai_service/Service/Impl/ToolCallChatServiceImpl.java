package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;
import com.atlas_erp.ai_service.prompt.DatabaseSchemaProvider;
import com.atlas_erp.ai_service.tools.HrTools;
import com.atlas_erp.ai_service.Service.ToolCallChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Custom implementation của ToolCallChatService
 * Sử dụng WebClient trực tiếp thay vì Spring AI ChatClient
 * 
 * Lý do: StepFun provider không hỗ trợ role="tool" trong conversation history
 * Giải pháp: Chuyển tool result thành assistant message
 */
@Slf4j
@Service
public class ToolCallChatServiceImpl implements ToolCallChatService {

        private final HrTools hrTools;
        private final DatabaseSchemaProvider schemaProvider;
        private final String defaultModel;
        private final WebClient webClient;
        private final ObjectMapper objectMapper;

        public ToolCallChatServiceImpl(
                        HrTools hrTools,
                        DatabaseSchemaProvider schemaProvider,
                        @Value("${spring.ai.openai.chat.options.model:stepfun/step-3.5-flash:free}") String defaultModel,
                        @Value("${spring.ai.openai.base-url:https://openrouter.ai/api}") String baseUrl,
                        @Value("${spring.ai.openai.api-key:}") String apiKey) {

                this.hrTools = hrTools;
                this.schemaProvider = schemaProvider;
                this.defaultModel = defaultModel;
                this.objectMapper = new ObjectMapper();

                // Build WebClient for direct API calls
                this.webClient = WebClient.builder()
                                .baseUrl(baseUrl)
                                .defaultHeader("Authorization", "Bearer " + apiKey)
                                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                                .defaultHeader("X-Title", "AI-Service")
                                .build();

                log.info("Custom ToolCallChatService initialized with model: {}", defaultModel);
                log.info("WebClient initialized with baseUrl: {}", baseUrl);
        }

        @Override
        public ChatResponse chatWithTools(ChatRequest chatRequest) {
                log.info("Processing chat with custom tools: question={}", chatRequest.getQuestion());

                try {
                        String systemPrompt = buildSystemPrompt();

                        log.debug("=== CUSTOM AGENT LOOP START ===");
                        log.debug("Model: {}", defaultModel);
                        log.debug("User Question: {}", chatRequest.getQuestion());

                        // Build initial messages
                        List<Map<String, Object>> messages = new ArrayList<>();

                        // System message
                        Map<String, Object> systemMsg = new HashMap<>();
                        systemMsg.put("role", "system");
                        systemMsg.put("content", systemPrompt);
                        messages.add(systemMsg);

                        // User message
                        Map<String, Object> userMsg = new HashMap<>();
                        userMsg.put("role", "user");
                        userMsg.put("content", chatRequest.getQuestion());
                        messages.add(userMsg);

                        // Custom agent loop
                        String finalResponse = executeAgentLoop(messages);

                        log.info("Tool call chat response generated successfully");

                        return ChatResponse.builder()
                                        .answer(finalResponse)
                                        .sessionId(chatRequest.getSessionId() != null
                                                        ? chatRequest.getSessionId()
                                                        : UUID.randomUUID().toString())
                                        .timestamp(System.currentTimeMillis())
                                        .model(defaultModel)
                                        .build();

                } catch (Exception e) {
                        log.error("Error processing tool call chat: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to process chat with tools: " + e.getMessage(), e);
                }
        }

        /**
         * Custom Agent Loop
         * 1. Gọi LLM với messages hiện tại
         * 2. Nếu có tool_calls -> execute tool -> thêm assistant message với tool
         * result
         * 3. Lặp lại cho đến khi không còn tool_calls
         */
        private String executeAgentLoop(List<Map<String, Object>> messages) throws JsonProcessingException {
                int maxIterations = 10;
                int iteration = 0;

                while (iteration < maxIterations) {
                        iteration++;
                        log.debug("Agent loop iteration: {}", iteration);

                        // Gọi LLM và parse response thành JsonNode
                        String responseStr = callLLMToString(messages);
                        JsonNode jsonResponse = objectMapper.readTree(responseStr);

                        // Lấy assistant message từ response
                        JsonNode choices = jsonResponse.get("choices");
                        if (choices == null || choices.isEmpty() || !choices.isArray()) {
                                log.warn("No choices in LLM response");
                                break;
                        }

                        JsonNode firstChoice = choices.get(0);
                        JsonNode messageNode = firstChoice.get("message");

                        if (messageNode == null) {
                                log.warn("No message in LLM response");
                                break;
                        }

                        String content = messageNode.has("content") ? messageNode.get("content").asText() : "";
                        JsonNode toolCalls = messageNode.get("tool_calls");

                        // Thêm assistant message vào conversation history
                        Map<String, Object> assistantMsg = new HashMap<>();
                        assistantMsg.put("role", "assistant");
                        assistantMsg.put("content", content);

                        // Nếu có tool_calls, thêm vào message
                        if (toolCalls != null && !toolCalls.isEmpty()) {
                                List<Map<String, Object>> toolCallsList = new ArrayList<>();
                                for (JsonNode tc : toolCalls) {
                                        Map<String, Object> tcMap = new HashMap<>();
                                        tcMap.put("id", tc.get("id").asText());
                                        tcMap.put("type", tc.get("type").asText());

                                        JsonNode function = tc.get("function");
                                        Map<String, Object> funcMap = new HashMap<>();
                                        funcMap.put("name", function.get("name").asText());
                                        funcMap.put("arguments", function.get("arguments").asText());
                                        tcMap.put("function", funcMap);

                                        toolCallsList.add(tcMap);
                                }
                                assistantMsg.put("tool_calls", toolCallsList);
                        }

                        messages.add(assistantMsg);

                        // Nếu không có tool_calls, trả về response
                        if (toolCalls == null || toolCalls.isEmpty()) {
                                log.debug("No more tool calls, returning response");
                                return content.isEmpty() ? "Tôi đã xử lý yêu cầu của bạn." : content;
                        }

                        // Execute each tool call
                        for (JsonNode toolCall : toolCalls) {
                                String toolCallId = toolCall.get("id").asText();
                                String functionName = toolCall.get("function").get("name").asText();
                                String arguments = toolCall.get("function").get("arguments").asText();

                                log.info("Executing tool: {} with args: {}", functionName, arguments);

                                // Execute tool
                                String toolResult = executeTool(functionName, arguments);

                                // THAY VÌ dùng role="tool", dùng role="assistant" với content mô tả kết quả
                                // Đây là cách giải quyết vấn đề StepFun không hỗ trợ role="tool"
                                Map<String, Object> toolResultMsg = new HashMap<>();
                                toolResultMsg.put("role", "assistant");
                                toolResultMsg.put("content", "Tool " + functionName + " returned: " + toolResult);

                                messages.add(toolResultMsg);

                                log.debug("Tool result added as assistant message: {}", toolResultMsg);
                        }
                }

                log.warn("Max iterations reached ({})", maxIterations);
                return "Đã đạt giới hạn số lần xử lý. Vui lòng thử lại.";
        }

        /**
         * Gọi LLM qua OpenRouter API - trả về String
         */
        private String callLLMToString(List<Map<String, Object>> messages) throws JsonProcessingException {
                // Build request body
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", defaultModel);
                requestBody.put("messages", messages);
                requestBody.put("temperature", 0.7);

                // Thêm tools nếu cần
                if (shouldIncludeTools(messages)) {
                        requestBody.put("tools", buildToolsSchema());
                }

                String jsonBody = objectMapper.writeValueAsString(requestBody);
                log.debug("LLM Request: {}", jsonBody);

                // Send request
                String response = webClient.post()
                                .uri("/v1/chat/completions")
                                .bodyValue(requestBody)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                log.debug("LLM Response: {}", response);

                // Parse response to check for errors
                JsonNode jsonResponse = objectMapper.readTree(response);

                // Check for errors
                if (jsonResponse.has("error")) {
                        String errorMsg = jsonResponse.get("error").toString();
                        log.error("LLM API error: {}", errorMsg);
                        throw new RuntimeException("LLM API error: " + errorMsg);
                }

                return response;
        }

        /**
         * Kiểm tra xem có nên include tools trong request không
         * Chỉ include tools khi chưa có kết quả tool
         */
        private boolean shouldIncludeTools(List<Map<String, Object>> messages) {
                // Luôn include tools trong request đầu tiên
                // Sau đó LLM sẽ quyết định có gọi tool hay không
                return true;
        }

        /**
         * Build tools schema theo OpenAI format
         */
        private List<Map<String, Object>> buildToolsSchema() {
                List<Map<String, Object>> tools = new ArrayList<>();

                // Tool: query_hr_database
                Map<String, Object> queryTool = new HashMap<>();
                queryTool.put("type", "function");

                Map<String, Object> queryFunction = new HashMap<>();
                queryFunction.put("name", "query_hr_database");
                queryFunction.put("description",
                                "Thực thi câu truy vấn SQL để lấy dữ liệu HR. CHỈ sử dụng câu lệnh SELECT (read-only). Không được phép sử dụng INSERT, UPDATE, DELETE, DROP, TRUNCATE.");
                queryFunction.put("parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                                "sql", Map.of(
                                                                "type", "string",
                                                                "description", "Câu truy vấn SQL (CHỈ SELECT)")),
                                "required", List.of("sql")));

                queryTool.put("function", queryFunction);
                tools.add(queryTool);

                // Tool: search_employees
                Map<String, Object> searchTool = new HashMap<>();
                searchTool.put("type", "function");

                Map<String, Object> searchFunction = new HashMap<>();
                searchFunction.put("name", "search_employees");
                searchFunction.put("description",
                                "Tìm kiếm nhân viên theo tên. Trả về thông tin cơ bản: id, employee_code, full_name, work_email, phone, status, hire_date.");
                searchFunction.put("parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                                "name", Map.of(
                                                                "type", "string",
                                                                "description",
                                                                "Tên nhân viên cần tìm (có thể là từng phần)")),
                                "required", List.of("name")));

                searchTool.put("function", searchFunction);
                tools.add(searchTool);

                // Tool: get_today_attendance
                Map<String, Object> todayAttendanceTool = new HashMap<>();
                todayAttendanceTool.put("type", "function");

                Map<String, Object> todayAttendanceFunction = new HashMap<>();
                todayAttendanceFunction.put("name", "get_today_attendance");
                todayAttendanceFunction.put("description",
                                "Lấy danh sách chấm công hôm nay. Bao gồm: employee_id, full_name, check_in, check_out, status, worked_hours.");
                todayAttendanceFunction.put("parameters", Map.of(
                                "type", "object",
                                "properties", Map.of()));

                todayAttendanceTool.put("function", todayAttendanceFunction);
                tools.add(todayAttendanceTool);

                // Tool: get_employee_list
                Map<String, Object> employeeListTool = new HashMap<>();
                employeeListTool.put("type", "function");

                Map<String, Object> employeeListFunction = new HashMap<>();
                employeeListFunction.put("name", "get_employee_list");
                employeeListFunction.put("description", "Lấy danh sách nhân viên với giới hạn số lượng.");
                employeeListFunction.put("parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                                "limit", Map.of(
                                                                "type", "integer",
                                                                "description",
                                                                "Số lượng nhân viên tối đa (mặc định 20)"))));

                employeeListTool.put("function", employeeListFunction);
                tools.add(employeeListTool);

                // Tool: get_employee_statistics
                Map<String, Object> statsTool = new HashMap<>();
                statsTool.put("type", "function");

                Map<String, Object> statsFunction = new HashMap<>();
                statsFunction.put("name", "get_employee_statistics");
                statsFunction.put("description",
                                "Lấy thống kê tổng quan về nhân viên. Bao gồm: tổng số, theo trạng thái (ACTIVE, PROBATION, SUSPENDED, etc.)");
                statsFunction.put("parameters", Map.of(
                                "type", "object",
                                "properties", Map.of()));

                statsTool.put("function", statsFunction);
                tools.add(statsTool);

                // Tool: get_attendance_by_date
                Map<String, Object> attendanceByDateTool = new HashMap<>();
                attendanceByDateTool.put("type", "function");

                Map<String, Object> attendanceByDateFunction = new HashMap<>();
                attendanceByDateFunction.put("name", "get_attendance_by_date");
                attendanceByDateFunction.put("description",
                                "Lấy danh sách chấm công theo ngày cụ thể. Ngày format: YYYY-MM-DD (ví dụ: 2024-01-15)");
                attendanceByDateFunction.put("parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                                "date", Map.of(
                                                                "type", "string",
                                                                "description",
                                                                "Ngày cần lấy thông tin (format: YYYY-MM-DD)")),
                                "required", List.of("date")));

                attendanceByDateTool.put("function", attendanceByDateFunction);
                tools.add(attendanceByDateTool);

                return tools;
        }

        /**
         * Execute tool bằng reflection
         */
        private String executeTool(String functionName, String arguments) {
                // Map snake_case tool name to camelCase method name
                String methodName = convertToCamelCase(functionName);
                log.debug("Looking for method: {} (from tool: {})", methodName, functionName);

                try {
                        // Parse arguments
                        Map<String, Object> argsMap;
                        try {
                                argsMap = objectMapper.readValue(arguments, Map.class);
                        } catch (Exception e) {
                                log.warn("Failed to parse arguments as JSON object: {}", arguments);
                                argsMap = new HashMap<>();
                        }

                        // Find method in hrTools bean (not class) using the bean
                        Method[] methods = hrTools.getClass().getDeclaredMethods();
                        for (Method method : methods) {
                                if (method.getName().equals(methodName)) {
                                        log.debug("Found method: {} with {} parameters", methodName,
                                                        method.getParameterCount());

                                        // Build parameter values
                                        List<Object> paramValues = new ArrayList<>();
                                        Class<?>[] paramTypes = method.getParameterTypes();

                                        for (int i = 0; i < paramTypes.length; i++) {
                                                Class<?> paramType = paramTypes[i];
                                                String argName = getArgNameForMethod(methodName, i);
                                                Object value = argsMap.get(argName);

                                                if (paramType == String.class) {
                                                        paramValues.add(value != null ? value.toString() : "");
                                                } else if (paramType == int.class || paramType == Integer.class) {
                                                        if (value != null) {
                                                                paramValues.add(Integer.parseInt(value.toString()));
                                                        } else {
                                                                paramValues.add(20); // default
                                                        }
                                                } else {
                                                        paramValues.add(null);
                                                }
                                        }

                                        // Invoke method
                                        method.setAccessible(true);
                                        Object result = method.invoke(hrTools, paramValues.toArray());
                                        return result != null ? result.toString() : "null";
                                }
                        }

                        log.error("Method not found: {} (tool: {})", methodName, functionName);
                        return "Error: Tool not found: " + functionName;

                } catch (Exception e) {
                        log.error("Error executing tool {}: {}", functionName, e.getMessage(), e);
                        return "Error executing tool: " + e.getMessage();
                }
        }

        /**
         * Convert snake_case to camelCase
         */
        private String convertToCamelCase(String snakeCase) {
                if (snakeCase == null || !snakeCase.contains("_")) {
                        return snakeCase;
                }
                StringBuilder result = new StringBuilder();
                boolean nextUpperCase = false;
                for (char c : snakeCase.toCharArray()) {
                        if (c == '_') {
                                nextUpperCase = true;
                        } else {
                                if (nextUpperCase) {
                                        result.append(Character.toUpperCase(c));
                                        nextUpperCase = false;
                                } else {
                                        result.append(c);
                                }
                        }
                }
                return result.toString();
        }

        /**
         * Get argument name for method based on method name
         */
        private String getArgNameForMethod(String methodName, int paramIndex) {
                // Map method name to expected argument names
                Map<String, String[]> methodArgs = new HashMap<>();
                methodArgs.put("queryHrDatabase", new String[] { "sql" });
                methodArgs.put("searchEmployees", new String[] { "name" });
                methodArgs.put("getTodayAttendance", new String[] {});
                methodArgs.put("getEmployeeList", new String[] { "limit" });
                methodArgs.put("getEmployeeStatistics", new String[] {});
                methodArgs.put("getAttendanceByDate", new String[] { "date" });

                String[] args = methodArgs.get(methodName);
                if (args != null && paramIndex < args.length) {
                        return args[paramIndex];
                }
                return "arg" + paramIndex;
        }

        /**
         * Build system prompt với schema information
         */
        // private String buildSystemPrompt() {
        // return """
        // Bạn là trợ lý AI cho hệ thống quản lý nhân sự Atlas ERP.

        // Nhiệm vụ của bạn:
        // 1. Hiểu câu hỏi của người dùng về dữ liệu nhân sự
        // 2. Sử dụng các tools được cung cấp để truy vấn database khi cần thiết
        // 3. Tổng hợp kết quả và trả lời bằng tiếng Việt

        // Các tools có sẵn:
        // - query_hr_database: Thực thi câu SQL (SELECT) để lấy dữ liệu
        // - search_employees: Tìm kiếm nhân viên theo tên
        // - get_today_attendance: Lấy danh sách chấm công hôm nay
        // - get_employee_list: Lấy danh sách nhân viên
        // - get_employee_statistics: Lấy thống kê nhân viên
        // - get_attendance_by_date: Lấy chấm công theo ngày cụ thể

        // Nguyên tắc trả lời:
        // - Khi người dùng hỏi về số liệu, hãy gọi tool để lấy dữ liệu chính xác
        // - Trả lời ngắn gọn, rõ ràng
        // - Nếu không tìm thấy dữ liệu, nói rõ "Không tìm thấy dữ liệu..."
        // - Nếu câu hỏi không liên quan đến HR, trả lời dựa trên kiến thức chung

        // """ + schemaProvider.getHrDatabaseSchema();
        // }

        private String buildSystemPrompt() {
                return """
                                Bạn là AI Assistant của hệ thống quản lý doanh nghiệp Atlas ERP.

                                🎯 Vai trò của bạn
                                Bạn hoạt động như một **chuyên gia phân tích dữ liệu doanh nghiệp (Enterprise Data Analyst)**.
                                Trước khi trả lời bất kỳ câu hỏi nào, hãy **phân tích vấn đề một cách logic và có hệ thống** giống như một chuyên gia dữ liệu.

                                Quy trình tư duy bắt buộc:
                                1️⃣ Phân tích câu hỏi của người dùng
                                - Người dùng đang hỏi về dữ liệu gì?
                                - Dữ liệu đó thuộc module nào trong hệ thống HR?

                                2️⃣ Xác định nguồn dữ liệu
                                - Dữ liệu có tồn tại trong database hay không
                                - Có cần truy vấn database để trả lời chính xác không

                                3️⃣ Lựa chọn công cụ phù hợp
                                - Nếu cần dữ liệu → chọn tool phù hợp nhất
                                - Nếu không cần dữ liệu → trả lời trực tiếp, giải thích chi tiết như chuyên gia đầu ngành

                                4️⃣ Tổng hợp kết quả
                                - Phân tích dữ liệu trả về
                                - Trình bày câu trả lời rõ ràng, dễ hiểu

                                --------------------------------------------------

                                🧰 Các tools có sẵn

                                - query_hr_database
                                  → Thực thi câu SQL (SELECT) để truy vấn dữ liệu HR

                                - search_employees
                                  → Tìm nhân viên theo tên

                                - get_today_attendance
                                  → Lấy dữ liệu chấm công hôm nay

                                - get_employee_list
                                  → Lấy danh sách toàn bộ nhân viên

                                - get_employee_statistics
                                  → Lấy các số liệu thống kê nhân sự

                                - get_attendance_by_date
                                  → Lấy dữ liệu chấm công theo ngày cụ thể

                                --------------------------------------------------

                                🧠 Chiến lược sử dụng tools

                                🔎 Tìm kiếm nhân viên theo tên
                                → dùng: search_employees

                                📊 Câu hỏi thống kê / tổng hợp dữ liệu
                                → ưu tiên: query_hr_database

                                📅 Chấm công hôm nay
                                → dùng: get_today_attendance

                                📆 Chấm công theo ngày cụ thể
                                → dùng: get_attendance_by_date

                                👥 Danh sách nhân viên
                                → dùng: get_employee_list

                                📈 Số liệu tổng quan nhân sự
                                → dùng: get_employee_statistics

                                --------------------------------------------------

                                🗄️ Nguyên tắc truy vấn database

                                - Chỉ được sử dụng câu SQL dạng: SELECT
                                - Không sử dụng INSERT, UPDATE, DELETE, DROP hoặc ALTER
                                - Truy vấn phải rõ ràng và tối ưu
                                - Chỉ lấy những cột dữ liệu cần thiết
                                - Khi thống kê nên dùng:
                                  COUNT, SUM, AVG, GROUP BY
                                - Khi cần lọc dữ liệu hãy dùng WHERE
                                - Không truy vấn toàn bộ bảng nếu không cần thiết

                                --------------------------------------------------

                                💬 Nguyên tắc trả lời

                                Khi trả lời cho người dùng:

                                ✔ Luôn sử dụng **icon và định dạng rõ ràng**
                                ✔ Trình bày đầy đủ và chi tiết thông tin
                                ✔ Nếu có dữ liệu → giải thích ý nghĩa của dữ liệu

                                Ví dụ format trả lời:

                                📊 **Kết quả thống kê**

                                • Tổng số nhân viên: 120
                                • Nhân viên đi làm hôm nay: 110
                                • Nhân viên nghỉ: 10

                                📌 Nhận xét:
                                Tỷ lệ đi làm hôm nay đạt **91.6%**.

                                --------------------------------------------------

                                ❗ Quy tắc quan trọng

                                - Nếu câu hỏi liên quan đến dữ liệu hệ thống → luôn gọi tool
                                - Không tự suy đoán dữ liệu khi chưa truy vấn
                                - Sau khi nhận dữ liệu → phải diễn giải cho người dùng
                                - Nếu không tìm thấy dữ liệu → trả lời:

                                "⚠️ Không tìm thấy dữ liệu phù hợp trong hệ thống."

                                - Nếu câu hỏi không liên quan đến hệ thống HR → trả lời dựa trên kiến thức về lĩnh vực đó

                                --------------------------------------------------

                                Dưới đây là schema database HR để bạn tham chiếu khi tạo truy vấn SQL:

                                """
                                + schemaProvider.getHrDatabaseSchema();
        }
}
