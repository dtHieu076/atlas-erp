package com.atlas_erp.accounting_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class TrialBalanceResponse {

    private String period;
    private List<TrialBalanceAccount> accounts;

    @Data
    public static class TrialBalanceAccount {
        private String accountId;
        private BigDecimal openingBalance;
        private BigDecimal totalDebit;
        private BigDecimal totalCredit;
        private BigDecimal closingBalance;
    }
}
