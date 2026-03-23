package com.atlas_erp.user_activity_service.repository;

import com.atlas_erp.user_activity_service.document.UserActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {
    List<UserActivity> findBySessionId(String sessionId);

    List<UserActivity> findByUserIdAndAction(String userId, String action);

    List<UserActivity> findByServerTimestampBetween(Instant start, Instant end);
    // Comment: Custom queries phổ biến cho audit ERP
}
