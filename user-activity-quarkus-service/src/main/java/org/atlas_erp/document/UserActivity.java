package org.atlas_erp.document;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Map;

@Data
@MongoEntity(collection = "erp_audit_logs") // Đã khớp với tên collection của bạn
public class UserActivity {

    @BsonId
    private ObjectId id; // Nên dùng ObjectId cho MongoDB thay vì String

    private String sessionId; // Truy vết phiên làm việc
    private String userId; // ID nhân viên
    private String action; // LOGIN, EXPORT_REPORT...
    private String page;
    private long clientTimestamp; // ms từ client
    private Instant serverTimestamp; // Thời gian server nhận
    private Map<String, Object> metadata; // Linh hoạt cho ERP data
    private String ipAddress;
    private String userAgent;
    private String userName;

}