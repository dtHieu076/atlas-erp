package org.atlas_erp.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.atlas_erp.document.UserActivity;
import org.bson.Document;

import com.mongodb.client.model.IndexOptions;

@ApplicationScoped
public class UserActivityRepository implements PanacheMongoRepository<UserActivity> {

    // Sự kiện này chạy 1 lần duy nhất lúc ứng dụng start
    void onStart(@Observes StartupEvent ev) {
        // 1. Tạo các Index đơn lẻ (Tương đương @Indexed)
        mongoCollection().createIndex(new Document("sessionId", 1));
        mongoCollection().createIndex(new Document("userId", 1));
        mongoCollection().createIndex(new Document("action", 1));
        mongoCollection().createIndex(new Document("serverTimestamp", 1));
        mongoCollection().createIndex(new Document("userName", 1));

        // 2. Tạo Compound Index: user_time_idx (Tương đương @CompoundIndex)
        mongoCollection().createIndex(
                new Document("userId", 1).append("clientTimestamp", -1),
                new IndexOptions().name("user_time_idx"));

        // 3. Tạo Compound Index: session_time_idx (Tương đương @CompoundIndex)
        mongoCollection().createIndex(
                new Document("sessionId", 1).append("clientTimestamp", -1),
                new IndexOptions().name("session_time_idx"));
    }
}