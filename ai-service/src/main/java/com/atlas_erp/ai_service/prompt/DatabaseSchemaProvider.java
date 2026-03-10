package com.atlas_erp.ai_service.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Cung cấp database schema information cho LLM
 * Giúp LLM hiểu cấu trúc database để sinh SQL queries chính xác
 */
@Slf4j
@Component
public class DatabaseSchemaProvider {

    /**
     * Lấy schema description cho HR database
     * Được sử dụng trong system prompt
     */
    public String getHrDatabaseSchema() {
        return """
                ### HR DATABASE SCHEMA ###

                #### Table: employee (singular form)
                | Column | Type | Description |
                |--------|------|-------------|
                | id | UUID | Primary key |
                | employee_code | VARCHAR | Mã nhân viên (unique) |
                | full_name | VARCHAR | Họ và tên đầy đủ |
                | gender | VARCHAR | Giới tính (MALE, FEMALE, OTHER) |
                | dob | DATE | Ngày sinh |
                | national_id | VARCHAR | Số CCCD |
                | phone | VARCHAR | Số điện thoại |
                | personal_email | VARCHAR | Email cá nhân |
                | work_email | VARCHAR | Email công việc |
                | permanent_address | TEXT | Địa chỉ thường trú |
                | current_address | TEXT | Địa chỉ hiện tại |
                | hometown | VARCHAR | Quê quán |
                | marital_status | VARCHAR | Tình trạng hôn nhân |
                | dependents_count | INTEGER | Số người phụ thuộc |
                | hire_date | DATE | Ngày vào làm |
                | probation_end_date | DATE | Ngày kết thúc thử việc |
                | employment_type | VARCHAR | Loại hình lao động |
                | status | VARCHAR | Trạng thái: ACTIVE, PROBATION, INACTIVE, TERMINATED |
                | avatar_url | TEXT | URL avatar |
                | manager_id | UUID | ID quản lý |
                | created_at | TIMESTAMP | Thời gian tạo |
                | updated_at | TIMESTAMP | Thời gian cập nhật |

                #### Table: attendance (singular form)
                | Column | Type | Description |
                |--------|------|-------------|
                | id | UUID | Primary key |
                | employee_id | UUID | FK to employee |
                | work_date | DATE | Ngày làm việc |
                | shift_id | UUID | ID ca làm việc |
                | check_in | TIMESTAMP | Giờ vào |
                | check_out | TIMESTAMP | Giờ ra |
                | worked_hours | DECIMAL | Số giờ làm việc |
                | overtime_hours | DECIMAL | Số giờ tăng ca |
                | work_minutes | INTEGER | Số phút làm việc |
                | ot_minutes | INTEGER | Số phút tăng ca |
                | late_minutes | INTEGER | Số phút đi muộn |
                | early_leave_minutes | INTEGER | Số phút về sớm |
                | status | VARCHAR | Trạng thái: WORKING, ABSENT, LEAVE, LATE |
                | leave_type | VARCHAR | Loại nghỉ (nếu có) |
                | note | VARCHAR | Ghi chú |
                | locked | BOOLEAN | Đã khóa chấm công |
                | created_at | TIMESTAMP | Thời gian tạo |
                | updated_at | TIMESTAMP | Thời gian cập nhật |

                ### IMPORTANT NOTES ###
                - Table names are in SINGULAR form: use 'employee' NOT 'employees'
                - When calculating age from birth date, use: EXTRACT(YEAR FROM AGE(dob))
                - Current date use: CURRENT_DATE
                - Only use SELECT statements (read-only)
                - Results returned as JSON array
                """;
    }

    /**
     * Lấy system prompt hoàn chỉnh cho Tool Calling
     */
    public String getSystemPrompt() {
        return """
                Bạn là trợ lý AI cho hệ thống quản lý nhân sự Atlas ERP.

                Nhiệm vụ của bạn:
                1. Hiểu câu hỏi của người dùng về dữ liệu nhân sự
                2. Sử dụng các tools được cung cấp để truy vấn database
                3. Tổng hợp kết quả và trả lời bằng tiếng Việt

                Cách sử dụng tools:
                - Khi người dùng hỏi về thông tin nhân viên, sử dụng search_employees hoặc query_hr_database
                - Khi người dùng hỏi về chấm công hôm nay, sử dụng get_today_attendance
                - Khi người dùng hỏi về số liệu thống kê, sử dụng query_hr_database với câu SELECT phù hợp

                Nguyên tắc trả lời:
                - Trả lời ngắn gọn, rõ ràng
                - Nếu không tìm thấy dữ liệu, nói rõ "Không tìm thấy dữ liệu..."
                - Nếu câu hỏi không liên quan đến HR, trả lời dựa trên kiến thức chung
                - Sử dụng định dạng bảng nếu kết quả có nhiều cột

                """ + getHrDatabaseSchema();
    }
}
