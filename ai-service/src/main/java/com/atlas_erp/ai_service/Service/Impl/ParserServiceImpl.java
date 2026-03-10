package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Dto.Response.IdCardData;
import com.atlas_erp.ai_service.Service.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser Service implementation using Regex patterns for Vietnamese ID Card
 * (CCCD)
 */
@Slf4j
@Service
public class ParserServiceImpl implements ParserService {

    // ==================== REGEX PATTERNS FOR FRONT SIDE ====================

    // Họ và tên patterns
    private static final Pattern[] NAME_PATTERNS = {
            Pattern.compile("(?:Họ\\s*và\\s*tên|Họ\\s*tên|Full\\s*name)[:\\s]*(.+?)(?:\\n|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("(?:Họ\\s*:?\\s*)(.+?)(?:\\n|$)", Pattern.CASE_INSENSITIVE),
            Pattern.compile(
                    "\\b([A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝĂĐĨŨƠÀẰẮẦẲẴẶẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪỬỮỰỲỴỶỸ][a-zàáâãèéêìíòóôõùúýăđĩũơàằắầẳẵặấầẩẫậắằẳẵặẹẻẽềềểễệỉịọỏốồổỗộớờởỡợụủứừửữựỳỵỷỹ]+(?:\\s+[A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝĂĐĨŨƠÀẰẮẦẲẴẶẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪỬỮỰỲỴỶỸ][a-zàáâãèéêìíòóôõùúýăđĩũơàằắầẳẵặấầẩẫậắằẳẵặẹẻẽềềểễệỉịọỏốồổỗộớờởỡợụủứừửữựỳỵỷỹ]+)+)\\b",
                    Pattern.CASE_INSENSITIVE),
    };

    // Ngày sinh patterns
    private static final Pattern[] DOB_PATTERNS = {
            Pattern.compile("(?:Ngày\\s*sinh|DOB|Birth)[:\\s]*(\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2,4})",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2,4})\\b"),
    };

    // Giới tính patterns
    private static final Pattern[] GENDER_PATTERNS = {
            Pattern.compile("(?:Giới\\s*tính|Sex|Gender)[:\\s]*(Nam|Nữ|Nam\\s*giới|Nữ\\s*giới)",
                    Pattern.CASE_INSENSITIVE),
    };

    // Số CCCD/CMND patterns
    private static final Pattern[] ID_NUMBER_PATTERNS = {
            Pattern.compile("(?:Số|CMND|CCCD|ID\\s*No|ID\\s*Number)[:\\s]*(\\d{9,12})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(\\d{9})\\b"), // 9 digit CMND
            Pattern.compile("\\b(\\d{12})\\b"), // 12 digit CCCD
    };

    // Quê quán patterns
    private static final Pattern[] PLACE_OF_ORIGIN_PATTERNS = {
            Pattern.compile("(?:Quê\\s*quán|Place\\s*of\\s*origin)[:\\s]*(.+?)(?:\\n|Ngày|Nơi|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
    };

    // Nơi thường trú patterns
    private static final Pattern[] ADDRESS_PATTERNS = {
            Pattern.compile("(?:Nơi\\s*thường\\s*trú|Permanent\\s*address|Address)[:\\s]*(.+?)(?:\\n|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
    };

    // ==================== REGEX PATTERNS FOR BACK SIDE ====================

    // Ngày cấp patterns
    private static final Pattern[] ISSUE_DATE_PATTERNS = {
            Pattern.compile(
                    "(?:Ngày\\s*cấp|Issue\\s*Date|Date\\s*of\\s*issue)[:\\s]*(\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2,4})",
                    Pattern.CASE_INSENSITIVE),
    };

    // Nơi cấp patterns
    private static final Pattern[] ISSUE_PLACE_PATTERNS = {
            Pattern.compile("(?:Nơi\\s*cấp|Issue\\s*Place)[:\\s]*(.+?)(?:\\n|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
    };

    // Dân tộc patterns
    private static final Pattern[] ETHNICITY_PATTERNS = {
            Pattern.compile("(?:Dân\\s*tộc|Ethnicity)[:\\s]*(.+?)(?:,|\\n|$)", Pattern.CASE_INSENSITIVE),
    };

    // Tôn giáo patterns
    private static final Pattern[] RELIGION_PATTERNS = {
            Pattern.compile("(?:Tôn\\s*giáo|Religion|Faith)[:\\s]*(.+?)(?:,|\\n|$)", Pattern.CASE_INSENSITIVE),
    };

    @Override
    public IdCardData parseFrontCard(String rawText) {
        log.info("Parsing front side of ID card...");

        IdCardData data = IdCardData.builder()
                .rawOcrFront(rawText)
                .build();

        try {
            // Extract name
            data.setFullName(extractField(rawText, NAME_PATTERNS));

            // Extract date of birth
            data.setDateOfBirth(extractField(rawText, DOB_PATTERNS));

            // Extract gender
            data.setGender(extractField(rawText, GENDER_PATTERNS));

            // Extract ID number
            data.setIdNumber(extractField(rawText, ID_NUMBER_PATTERNS));

            // Extract place of origin
            data.setPlaceOfOrigin(extractField(rawText, PLACE_OF_ORIGIN_PATTERNS));

            // Extract address
            data.setAddress(extractField(rawText, ADDRESS_PATTERNS));

        } catch (Exception e) {
            log.error("Error parsing front card: {}", e.getMessage(), e);
        }

        log.info("Front side parsed: {}", data);
        return data;
    }

    @Override
    public IdCardData parseBackCard(String rawText) {
        log.info("Parsing back side of ID card...");

        IdCardData data = IdCardData.builder()
                .rawOcrBack(rawText)
                .build();

        try {
            // Extract issue date
            data.setIssueDate(extractField(rawText, ISSUE_DATE_PATTERNS));

            // Extract issue place
            data.setIssuePlace(extractField(rawText, ISSUE_PLACE_PATTERNS));

            // Extract ethnicity
            data.setEthnicity(extractField(rawText, ETHNICITY_PATTERNS));

            // Extract religion
            data.setReligion(extractField(rawText, RELIGION_PATTERNS));

        } catch (Exception e) {
            log.error("Error parsing back card: {}", e.getMessage(), e);
        }

        log.info("Back side parsed: {}", data);
        return data;
    }

    /**
     * Extract field using multiple regex patterns
     */
    private String extractField(String text, Pattern[] patterns) {
        for (Pattern pattern : patterns) {
            try {
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    String value = matcher.group(1).trim();
                    // Clean up the value
                    value = cleanValue(value);
                    if (value != null && !value.isEmpty()) {
                        log.debug("Extracted with pattern {}: {}", pattern.pattern(), value);
                        return value;
                    }
                }
            } catch (Exception e) {
                log.debug("Pattern {} failed: {}", pattern.pattern(), e.getMessage());
            }
        }
        return null;
    }

    /**
     * Clean extracted value
     */
    private String cleanValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // Remove extra whitespace
        value = value.replaceAll("\\s+", " ").trim();

        // Remove common OCR errors
        value = value.replace("|", "l")
                .replace("0", "O") // This might cause issues, be careful
                .replace("O", "0"); // Some OCR confuse O and 0

        // Remove leading/trailing special characters
        value = value.replaceAll("^[,:;-]+|[,:;-]+$", "");

        return value.trim();
    }
}
