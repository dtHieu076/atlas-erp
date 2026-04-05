package org.atlas_erp.service;

import org.atlas_erp.dto.ActivityLogRequest;
import org.atlas_erp.kafka.producer.LogProducer;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;

@ApplicationScoped
public class TrackingService {

    private final LogProducer logProducer;

    public TrackingService(LogProducer logProducer) {
        this.logProducer = logProducer;
    }

    public void trackActivity(ActivityLogRequest request, HttpServerRequest httpRequest) {
        if (request.getMetadata() == null) {
            request.setMetadata(new HashMap<>());
        } else {
            request.setMetadata(new HashMap<>(request.getMetadata()));
        }

        if (httpRequest != null) {
            request.getMetadata().put("ipAddress", getClientIpAddress(httpRequest));
            request.getMetadata().put("userAgent", httpRequest.getHeader("User-Agent"));
        }

        logProducer.sendLog(request);
    }

    private String getClientIpAddress(HttpServerRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.remoteAddress().host(); // Lấy IP bằng Vert.x
    }
}