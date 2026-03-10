# AI Service Tool Calling Refactoring TODO

## Mục tiêu
Refactor từ IntentClassifier + DatabaseReader → Spring AI Tool Calling (@Tool)

## Nguyên tắc thiết kế
1. LLM tự quyết định gọi tool nào
2. Ưu tiên tools tổng quát, không phải method cụ thể
3. Tools đại diện cho khả năng, không phải câu truy vấn cụ thể
4. Chỉ cho phép SELECT (read-only) để bảo mật

## Cấu trúc target
```
ai-service/src/main/java/com/atlas_erp/ai_service/
├── tools/
│   ├── HrTools.java              # HR domain tools (PRIORITY)
│   ├── InventoryTools.java       # Inventory tools (TƯƠNG LAI)
│   ├── OrganizationTools.java    # Organization tools (TƯƠNG LAI)
│   └── AccountingTools.java      # Accounting tools (TƯƠNG LAI)
├── prompt/
│   └── DatabaseSchemaProvider.java  # Schema descriptions for LLM
├── service/
│   ├── ToolCallChatService.java  # Interface mới
│   └── impl/
│       └── ToolCallChatServiceImpl.java
```

## Triển khai chi tiết

### Phase 1: Core Infrastructure ✅ COMPLETED
- [x] 1.1. Tạo DatabaseSchemaProvider - Cung cấp schema info cho LLM
- [x] 1.2. Tạo HrTools với @Tool annotations
      - query_hr_database(sql) - Generic SQL execution
      - search_employees(name) - Search by name
      - get_today_attendance() - Today's attendance
      - get_employee_list(limit) - Get employee list
      - get_employee_statistics() - Employee stats
      - get_attendance_by_date(date) - Attendance by date

### Phase 2: Service Layer ✅ COMPLETED
- [x] 2.1. Tạo ToolCallChatService interface
- [x] 2.2. Tạo ToolCallChatServiceImpl với ChatClient + tools registration

### Phase 3: Integration ✅ COMPLETED
- [x] 3.1. Thêm endpoint `/api/ai/chat/tools` vào AiChatController
- [x] 3.2. Tạo ToolCallChatService interface
- [x] 3.3. Tạo ToolCallChatServiceImpl implementation

### Phase 4: Mở rộng (Tương lai)
- [ ] 4.1. InventoryTools - Truy vấn tồn kho
- [ ] 4.2. OrganizationTools - Truy vấn phòng ban
- [ ] 4.3. AccountingTools - Truy vấn tài chính

## Tools Design (HrTools)

```java
@Tool(name = "query_hr_database", description = """
    Thực thi câu truy vấn SQL để lấy dữ liệu HR.
    CHỉ sử dụng câu lệnh SELECT (read-only).
    Ví dụ: SELECT * FROM employees WHERE status = 'ACTIVE'
    """)
String queryHrDatabase(@Param("sql") String sql);

@Tool(name = "search_employees", description = """
    Tìm kiếm nhân viên theo tên.
    Trả về thông tin cơ bản: id, name, email, department, status.
    """)
List<Map<String, Object>> searchEmployees(@Param("name") String name);

@Tool(name = "get_today_attendance", description = """
    Lấy danh sách chấm công hôm nay.
    Bao gồm: employee_id, name, check_in_time, status.
    """)
List<Map<String, Object>> getTodayAttendance();

@Tool(name = "get_employee_list", description = """
    Lấy danh sách nhân viên với giới hạn.
    """)
List<Map<String, Object>> getEmployeeList(@Param("limit") int limit);
```

## Database Schema cho LLM

```
employees:
- id (UUID, PRIMARY KEY)
- employee_code (VARCHAR)
- full_name (VARCHAR)
- gender (VARCHAR)
- dob (DATE)
- phone (VARCHAR)
- work_email (VARCHAR)
- department_id (UUID)
- status (VARCHAR) - ACTIVE, PROBATION, INACTIVE, TERMINATED
- hire_date (DATE)
- contract_end_date (DATE)

attendance:
- id (UUID, PRIMARY KEY)
- employee_id (UUID, FK)
- work_date (DATE)
- check_in (TIMESTAMP)
- check_out (TIMESTAMP)
- status (VARCHAR) - WORKING, ABSENT, LEAVE, LATE

leave_requests:
- id (UUID, PRIMARY KEY)
- employee_id (UUID, FK)
- leave_type (VARCHAR)
- start_date (DATE)
- end_date (DATE)
- status (VARCHAR) - PENDING, APPROVED, REJECTED
```

## Test Cases

| Câu hỏi | Tool mong đợi | SQL Generated |
|---------|---------------|---------------|
| "Có bao nhiêu nhân viên đang làm?" | queryHrDatabase | SELECT COUNT(*) FROM employees WHERE status = 'ACTIVE' |
| "Tìm nhân viên tên An" | searchEmployees | SELECT * FROM employees WHERE full_name LIKE '%An%' |
| "Hôm nay có ai đi làm không?" | getTodayAttendance | SELECT * FROM attendance WHERE work_date = TODAY |
| "Ai sắp hết hợp đồng?" | queryHrDatabase | SELECT * FROM employees WHERE contract_end_date < 30 days |
| "Độ tuổi trung bình?" | queryHrDatabase | SELECT AVG(age) FROM employees |

