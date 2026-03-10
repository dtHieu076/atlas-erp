package com.atlas_erp.ai_service.QueryProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Intent Classifier - Phân loại ý định người dùng
 */
@Slf4j
@Component
public class IntentClassifier {

    private static final Map<String, List<String>> KEYWORD_MAP = new HashMap<>();

    static {
        // Employee keywords
        KEYWORD_MAP.put("EMPLOYEE", Arrays.asList(
                "nhân viên", "employee", "nhân sự", "hr", "human resources",
                "danh sách", "list", "số lượng", "count", "total",
                "đang làm", "active", "đang làm việc", "probation", "thử việc"));

        // Attendance keywords
        KEYWORD_MAP.put("ATTENDANCE", Arrays.asList(
                "chấm công", "attendance", "điểm danh", "checkin", "checkout",
                "có mặt", "present", "vắng mặt", "absent", "nghỉ phép",
                "hôm nay", "today", "ngày", "date"));

        // Leave keywords
        KEYWORD_MAP.put("LEAVE", Arrays.asList(
                "nghỉ phép", "leave", "nghỉ", "off", "vacation",
                "xin nghỉ", "request leave", "đơn xin nghỉ", "pending",
                "chờ duyệt", "approved", "đã duyệt", "rejected", "từ chối"));

        // Contract keywords
        KEYWORD_MAP.put("CONTRACT", Arrays.asList(
                "hợp đồng", "contract", "ký hợp đồng", "hết hợp đồng"));

        // General keywords
        KEYWORD_MAP.put("GENERAL", Arrays.asList(
                "tổng quan", "overview", "summary", "thống kê", "statistics"));
    }

    /**
     * Classify user intent from question
     */
    public IntentResult classify(String question) {
        String lowerQuestion = question.toLowerCase();

        // Check each category
        int employeeScore = countMatches(lowerQuestion, KEYWORD_MAP.get("EMPLOYEE"));
        int attendanceScore = countMatches(lowerQuestion, KEYWORD_MAP.get("ATTENDANCE"));
        int leaveScore = countMatches(lowerQuestion, KEYWORD_MAP.get("LEAVE"));
        int contractScore = countMatches(lowerQuestion, KEYWORD_MAP.get("CONTRACT"));
        int generalScore = countMatches(lowerQuestion, KEYWORD_MAP.get("GENERAL"));

        // Find highest score
        int maxScore = Math.max(Math.max(employeeScore, attendanceScore),
                Math.max(leaveScore, Math.max(contractScore, generalScore)));

        String primaryIntent;
        if (maxScore == 0) {
            primaryIntent = "GENERAL";
        } else if (employeeScore == maxScore) {
            primaryIntent = "EMPLOYEE";
        } else if (attendanceScore == maxScore) {
            primaryIntent = "ATTENDANCE";
        } else if (leaveScore == maxScore) {
            primaryIntent = "LEAVE";
        } else if (contractScore == maxScore) {
            primaryIntent = "CONTRACT";
        } else {
            primaryIntent = "GENERAL";
        }

        // Determine specific action
        String action = determineAction(lowerQuestion, primaryIntent);

        log.info("Classified intent: {} with action: {}", primaryIntent, action);

        return new IntentResult(primaryIntent, action);
    }

    private int countMatches(String text, List<String> keywords) {
        int count = 0;
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                count++;
            }
        }
        return count;
    }

    private String determineAction(String question, String intent) {
        switch (intent) {
            case "EMPLOYEE":
                if (question.contains("số lượng") || question.contains("count") || question.contains("bao nhiêu")) {
                    return "COUNT_EMPLOYEES";
                } else if (question.contains("danh sách") || question.contains("list")) {
                    return "LIST_EMPLOYEES";
                }
                return "GET_EMPLOYEE_INFO";

            case "ATTENDANCE":
                if (question.contains("hôm nay") || question.contains("today")) {
                    return "TODAY_ATTENDANCE";
                } else if (question.contains("số lượng") || question.contains("bao nhiêu")) {
                    return "COUNT_ATTENDANCE";
                }
                return "GET_ATTENDANCE";

            case "LEAVE":
                if (question.contains("chờ duyệt") || question.contains("pending")) {
                    return "PENDING_LEAVES";
                } else if (question.contains("số lượng") || question.contains("bao nhiêu")) {
                    return "COUNT_LEAVES";
                }
                return "GET_LEAVE_INFO";

            case "CONTRACT":
                return "GET_CONTRACT_INFO";

            default:
                return "GENERAL";
        }
    }

    /**
     * Intent classification result
     */
    public record IntentResult(String intent, String action) {
    }
}
