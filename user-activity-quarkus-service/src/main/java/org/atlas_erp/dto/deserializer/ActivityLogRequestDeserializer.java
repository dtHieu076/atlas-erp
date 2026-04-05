package org.atlas_erp.dto.deserializer;

import org.atlas_erp.dto.ActivityLogRequest;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ActivityLogRequestDeserializer extends ObjectMapperDeserializer<ActivityLogRequest> {
    public ActivityLogRequestDeserializer() {
        super(ActivityLogRequest.class);
    }
}