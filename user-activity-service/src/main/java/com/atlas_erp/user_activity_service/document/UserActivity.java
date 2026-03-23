package com.atlas_erp.user_activity_service.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "erp_audit_logs")
public class UserActivity {
    @Id
    private String id;

    @Indexed
    private String sessionId; // Truy vết phiên làm việc

    @Indexed
    private String userId; // ID nhân viên

    @Indexed
    private String action; // LOGIN, EXPORT_REPORT...

    private String page;

    private long clientTimestamp; // ms từ client

    @Indexed
    private Instant serverTimestamp; // Thời gian server nhận

    private Map<String, Object> metadata; // Linh hoạt cho ERP data

    private String ipAddress;
    private String userAgent;

    // Comment: Document map với Mongo collection, indexes tối ưu query theo
    // session/user/action/time
}
