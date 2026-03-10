package com.atlas_erp.ai_service.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String answer;
    private String sessionId;
    private Long timestamp;
    private String model;
    private List<String> sources;
    private Map<String, Object> metadata;
}
