# CƠ SỞ LÝ THUYẾT VÀ KẾ HOẠCH TRIỂN KHAI

## 1. PHÂN TÍCH HIỆN TRẠNG

### 1.1. Tình huống hiện tại:
- **AI Service** hiện tại chỉ sử dụng **LLM (Large Language Model)** thông qua OpenRouter API để trả lời câu hỏi
- AI trả lời dựa trên kiến thức chung của mô hình, **không truy cập** dữ liệu thực từ hệ thống ERP
- Các service khác (HR, Inventory, Organization...) có đầy đủ Entity, Repository, Service để truy xuất dữ liệu

### 1.2. Vấn đề cần giải quyết:
AI cần trả lời các câu hỏi dựa trên dữ liệu thực như:
- "Có bao nhiêu nhân viên đang làm việc?"
- "Tồn kho của sản phẩm A là bao nhiêu?"
- "Danh sách phòng ban của công ty?"

---

## 2. CƠ SỞ LÝ THUYẾT

### 2.1. RAG (Retrieval-Augmented Generation)
Đây là phương pháp kết hợp:
- **Retrieval (Truy xuất)**: Lấy dữ liệu li quan từ database
- **Augmented (Bổ sung)**: Đưa dữ liệu vào prompt
- **Generation (Sinh)**: LLM tạo câu trả lời dựa trên dữ liệu

### 2.2. Các phương pháp tích hợp Database:

| Phương pháp | Độ phức tạp | Dữ liệu | Phù hợp |
|-------------|-------------|---------|---------|
| **SQL/API Query** | Thấp | Structured | Truy vấn cụ thể |
| **Vector Store** | Trung bình | Semi-structured | Tìm kiếm ngữ nghĩa |
| **Text-to-SQL** | Cao | Structured | Truy vấn tự nhiên |

### 2.3. Kiến trúc đề xuất: **Hybrid Approach**

```
User Query → AI Service → Intent Classification
                              ↓
                    ┌─────────┴─────────┐
                    ↓                   ↓
           [Database Query]    [LLM General]
                    ↓                   ↓
              Structured         General Info
                    └─────────┬─────────┘
                              ↓
                     Generate Response
```

---

## 3. KẾ HOẠCH TRIỂN KHAI CHI TIẾT

### **Lưu ý quan trọng:**
- **Demo Phase**: Kết nối trực tiếp đến **HR Database** với chuỗi kết nối **tĩnh** trong `application.properties`
- **Production Phase**: Sẽ cân nhắc sử dụng Config Server nếu cần thiết

---

### Giai đoạn 1: Chuẩn bị hạ tầng (Week 1-2)

**3.1.1. Cấu hình Database Connection cho AI Service (Static - Demo)**
- Thêm dependency PostgreSQL Driver
- Cấu hình DataSource kết nối trực tiếp đến HR Database
- **Không sử dụng Config Server** - cấu hình tĩnh trong `application.properties`

```properties
# HR Database Connection (Static - Demo)
spring.datasource.url=jdbc:postgresql://<host>/hr_db
spring.datasource.username=<username>
spring.datasource.password=<password>
```

**3.1.2. Tạo Module Data Retrieval**
```
ai-service/
├── DataReader/
│   ├── DatabaseReader.java          # Interface đọc DB
│   ├── SqlDatabaseReader.java       # Impl đọc PostgreSQL
│   └── ApiDatabaseReader.java       # Impl gọi API service (tương lai)
├── QueryProcessor/
│   ├── IntentClassifier.java        # Phân loại ý định
│   └── QueryGenerator.java          # Sinh SQL/API call
└── ContextBuilder/
    └── PromptContextBuilder.java    # Build context cho LLM
```

### Giai đoạn 2: Triển khai chức năng (Week 3-4)

**3.2.1. Intent Classification**
- Phân loại câu hỏi: HR (Employee, Attendance, Leave), General
- Xác định action cần thực hiện

**3.2.2. Data Retrieval Layer**
- **SQL-based**: Truy vấn trực tiếp HR Database
- Cache kết quả để giảm latency (tương lai)

**3.2.3. Context Building**
- Chuyển đổi kết quả DB thành text ngắn gọn
- Đưa vào prompt cho LLM

### Giai đoạn 3: Tối ưu hóa (Week 5-6)

**3.3.1. Error Handling**
- Xử lý trường hợp không tìm thấy dữ liệu
- Fallback sang LLM general khi DB error

**3.3.2. Performance**
- Cache thường xuyên query
- Batch processing cho nhiều câu hỏi

**3.3.3. Security**
- Validate input tránh SQL injection
- Giới hạn quyền đọc data

---

## 4. THÀNH PHẦN CẦN THIẾT

### 4.1. Dependencies trong pom.xml:
```xml
<!-- Database -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Redis Cache (tương lai) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 4.2. Cấu hình Application Properties (Static):
```properties
# ===============================
# HR Database Connection (Static - Demo)
# ===============================
spring.datasource.url=jdbc:postgresql://ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/hr_db?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_ODZx4pvMKCU1
spring.datasource.driver-class-name=org.postgresql.Driver

# ===============================
# JPA Configuration
# ===============================
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## 5. VÍ DỤ LUỒNG XỬ LÝ

**Câu hỏi**: "Có bao nhiêu nhân viên đang làm việc?"

```
1. AI Service nhận câu hỏi
2. Intent Classifier xác định: [HR] + [EMPLOYEE] + [COUNT]
3. SqlDatabaseReader thực thi SQL: "SELECT COUNT(*) FROM employees WHERE status = 'ACTIVE'"
4. Kết quả: 150 nhân viên
5. Prompt Context Builder tạo context: "Tổng nhân viên: 150, Active: 140, Probation: 10"
6. LLM sinh câu trả lời hoàn chỉnh: "Hiện tại hệ thống có 150 nhân viên đang làm việc..."
7. Trả về response cho user
```

---

## 6. CÁC API ENDPOINTS

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/ai/chat` | Chat với LLM (general - không truy vấn DB) |
| POST | `/api/ai/chat/database` | Chat với dữ liệu từ HR Database |
| POST | `/api/ai/chat/auto` | Tự động chọn chế độ (DB → General nếu lỗi) |
| GET | `/api/ai/health` | Health check |

---

## 7. TÓM TẮT

| Giai đoạn | Nội dung | Thời gian |
|-----------|----------|-----------|
| 1 | Chuẩn bị hạ tầng, kết nối HR Database (static) | 2 tuần |
| 2 | Triển khai Intent Classification & Data Retrieval | 2 tuần |
| 3 | Tối ưu hóa, Security, Testing | 2 tuần |

**Tổng thời gian dự kiến: 6 tuần**

---

## 8. CẤU TRÚC THƯ MỤC AI-SERVICE

```
ai-service/
├── src/main/java/com/atlas_erp/ai_service/
│   ├── AiServiceApplication.java
│   ├── Config/
│   │   └── CorsConfig.java
│   ├── ContextBuilder/
│   │   └── PromptContextBuilder.java       # Build context cho LLM
│   ├── Controller/
│   │   └── AiChatController.java            # REST API endpoints
│   ├── DataReader/
│   │   ├── DatabaseReader.java              # Interface
│   │   └── Impl/
│   │       └── SqlDatabaseReader.java       # SQL implementation
│   ├── Dto/
│   │   ├── Request/ChatRequest.java
│   │   └── Response/
│   │       ├── ApiResponse.java
│   │       └── ChatResponse.java
│   ├── Entity/
│   │   ├── AttendanceEntity.java
│   │   ├── EmployeeEntity.java
│   │   └── LeaveRequestEntity.java
│   ├── enums/
│   │   ├── AttendanceStatus.java
│   │   ├── EmploymentStatus.java
│   │   └── GenderType.java
│   ├── QueryProcessor/
│   │   └── IntentClassifier.java           # Phân loại intent
│   ├── Repository/
│   │   ├── AttendanceRepository.java
│   │   ├── EmployeeRepository.java
│   │   └── LeaveRequestRepository.java
│   └── Service/
│       ├── ChatRagService.java              # Interface RAG
│       ├── ChatService.java                 # Interface general
│       └── Impl/
│           ├── ChatRagServiceImpl.java      # RAG implementation
│           └── ChatServiceImpl.java         # General implementation
└── src/main/resources/
    └── application.properties                # Static DB config



API Endpoints:
1. Chat với Database (RAG)
Method: POST
URL: http://localhost:8080/api/ai/chat/database
Body (JSON):

{
    "question": "Có bao nhiêu nhân viên đang làm việc?"
}
2. Chat Auto (tự động chọn Database → General nếu lỗi)
Method: POST
URL: http://localhost:8080/api/ai/chat/auto
Body (JSON):

{
    "question": "Tổng số nhân viên là bao nhiêu?"
}
3. Chat General (không truy vấn database)
Method: POST
URL: http://localhost:8080/api/ai/chat
Body (JSON):

{
    "question": "Xin chào"
}
4. Health Check
Method: GET
URL: http://localhost:8080/api/ai/health
Ví dụ câu hỏi để test:
Câu hỏi	Mô tả
"Có bao nhiêu nhân viên đang làm việc?"	Đếm nhân viên theo trạng thái
"Tổng số nhân viên là bao nhiêu?"	Lấy tất cả nhân viên
"Hôm nay có bao nhiêu người đi làm?"	Xem điểm danh hôm nay
"Có bao nhiêu đơn xin nghỉ phép đang chờ?"	Đếm leave request PENDING
"Danh sách nghỉ phép đang chờ?"	Lấy chi tiết leave request