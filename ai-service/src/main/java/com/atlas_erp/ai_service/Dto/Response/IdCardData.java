package com.atlas_erp.ai_service.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structured data extracted from ID Card (CCCD)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardData {

    /**
     * Họ và tên đầy đủ
     */
    private String fullName;

    /**
     * Ngày sinh (format: dd/MM/yyyy)
     */
    private String dateOfBirth;

    /**
     * Giới tính (Nam/Nữ)
     */
    private String gender;

    /**
     * Số CCCD/CMND
     */
    private String idNumber;

    /**
     * Quê quán
     */
    private String placeOfOrigin;

    /**
     * Nơi thường trú
     */
    private String address;

    /**
     * Ngày cấp CCCD
     */
    private String issueDate;

    /**
     * Nơi cấp CCCD
     */
    private String issuePlace;

    /**
     * Dân tộc
     */
    private String ethnicity;

    /**
     * Tôn giáo
     */
    private String religion;

    /**
     * Raw OCR text từ mặt trước
     */
    private String rawOcrFront;

    /**
     * Raw OCR text từ mặt sau
     */
    private String rawOcrBack;

    /**
     * Câu văn format hoàn chỉnh
     */
    private String formattedText;
}
