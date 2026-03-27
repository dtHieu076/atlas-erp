package com.atlas_erp.user_activity_service.service;

import com.atlas_erp.user_activity_service.document.UserActivity;
import com.atlas_erp.user_activity_service.dto.ActivityLogQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogQueryService {

    private final MongoTemplate mongoTemplate;

    public Page<UserActivity> getActivityLogs(ActivityLogQueryRequest request, Pageable pageable) {
        Query query = new Query();

        // Thêm các điều kiện lọc tĩnh
        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            query.addCriteria(Criteria.where("userId").is(request.getUserId()));
        }
        if (request.getSessionId() != null && !request.getSessionId().isEmpty()) {
            query.addCriteria(Criteria.where("sessionId").is(request.getSessionId()));
        }
        if (request.getAction() != null && !request.getAction().isEmpty()) {
            query.addCriteria(Criteria.where("action").is(request.getAction()));
        }

        // Xử lý khoảng thời gian (Date Range) gộp vào chung 1 Criteria để tránh lỗi
        if (request.getStartDate() != null || request.getEndDate() != null) {
            Criteria dateCriteria = Criteria.where("clientTimestamp");
            if (request.getStartDate() != null) {
                dateCriteria.gte(request.getStartDate());
            }
            if (request.getEndDate() != null) {
                dateCriteria.lte(request.getEndDate());
            }
            query.addCriteria(dateCriteria);
        }

        // Đếm tổng số bản ghi (Count query)
        long total = mongoTemplate.count(query, UserActivity.class);

        // Apply phân trang và sắp xếp (Chỉ cần 1 dòng này là đủ)
        query.with(pageable);

        // Truy vấn dữ liệu
        List<UserActivity> content = mongoTemplate.find(query, UserActivity.class);

        return new PageImpl<>(content, pageable, total);
    }
}