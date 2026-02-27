package com.atlas_erp.hr_service.enums;

/**
 * Trạng thái hợp đồng lao động.
 */
public enum ContractStatus {

    DRAFT, // Hợp đồng đang soạn thảo
    ACTIVE, // Đang hiệu lực
    EXPIRED, // Hết hạn
    TERMINATED, // Chấm dứt trước hạn
    CANCELLED // Bị hủy
}