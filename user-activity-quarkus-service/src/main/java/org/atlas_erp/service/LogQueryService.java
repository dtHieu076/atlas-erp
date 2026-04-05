package org.atlas_erp.service;

import org.atlas_erp.document.UserActivity;
import org.atlas_erp.dto.ActivityLogQueryRequest;
import org.atlas_erp.repository.UserActivityRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LogQueryService {

    private final UserActivityRepository repository;

    public LogQueryService(UserActivityRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getActivityLogs(ActivityLogQueryRequest request, int pageIndex, int size,
            String sortField, String direction) {

        Document query = new Document();

        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            query.append("userId", request.getUserId());
        }
        if (request.getSessionId() != null && !request.getSessionId().isEmpty()) {
            query.append("sessionId", request.getSessionId());
        }
        if (request.getAction() != null && !request.getAction().isEmpty()) {
            query.append("action", request.getAction());
        }

        if (request.getStartDate() != null || request.getEndDate() != null) {
            Document dateQuery = new Document();
            if (request.getStartDate() != null) {
                dateQuery.append("$gte", request.getStartDate());
            }
            if (request.getEndDate() != null) {
                dateQuery.append("$lte", request.getEndDate());
            }
            query.append("clientTimestamp", dateQuery);
        }

        // SỬA TẠI ĐÂY: Dùng Document để sắp xếp thay vì io.quarkus.panache.common.Sort
        int sortOrder = direction.equalsIgnoreCase("ASC") ? 1 : -1;
        Document sortDoc = new Document(sortField, sortOrder);

        // Truy vấn dữ liệu truyền vào (Document, Document)
        PanacheQuery<UserActivity> panacheQuery = repository.find(query, sortDoc);
        panacheQuery.page(Page.of(pageIndex, size));

        List<UserActivity> content = panacheQuery.list();
        long total = panacheQuery.count();

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", total);
        result.put("totalPages", (int) Math.ceil((double) total / size));
        result.put("number", pageIndex);
        result.put("size", size);

        return result;
    }
}